# Deployment (GitHub Actions → EC2)

Deploy versioned Docker images from **GHCR** to a single **EC2** instance. This matches the release flow: CI publishes `ghcr.io/<owner>/coursivo_backend:X.Y.Z` after semantic-release creates tag `vX.Y.Z`.

```text
merge main → CI → Release (tag v1.2.0) → CI on tag (build image :1.2.0)
                                              ↓
                         Deploy workflow (manual or on GitHub Release)
                                              ↓
                         SSH to EC2 → docker pull → docker compose up
```

## Architecture

```text
Internet → (optional ALB :443) → EC2 :8080 (Docker)
                                      ↓
                                 Neon PostgreSQL
                                 SendGrid (optional)
```

- **EC2** runs only the API container; database stays on **Neon**.
- Bind app to `127.0.0.1:8080` in compose so the API is not wide-open if you add an ALB later.
- For a quick test without ALB, temporarily map `0.0.0.0:8080:8080` and restrict the security group to your IP.

## One-time EC2 setup

1. Launch **Ubuntu 22.04/24.04** (e.g. `t3.small`), with a security group allowing:
   - **SSH (22)** from your IP
   - **8080** from ALB or your IP (or only from ALB if using HTTPS in front)
2. Attach an **Elastic IP** (optional but recommended).
3. SSH in and run the bootstrap script from this repo:

   ```bash
   sudo bash deploy/ec2-bootstrap.sh
   ```

4. Create app config on the server:

   ```bash
   sudo cp deploy/.env.example /opt/coursivo/.env
   sudo nano /opt/coursivo/.env
   sudo chmod 600 /opt/coursivo/.env
   sudo chown $USER:$USER /opt/coursivo/.env
   ```

5. In **Neon**, allow connections from the EC2 public IP (or use Neon pooler + SSL).

6. Ensure the GHCR package is pullable:
   - **Public package:** any valid `GHCR_PAT` with `read:packages` works.
   - **Private package:** PAT must have access to the repo’s packages.

## GitHub configuration

### Environments and manual approval (deploy on release)

The Deploy workflow uses `environment: production`. That is how GitHub gates deploys behind a human.

1. Repo **Settings → Environments → New environment** → name it **`production`** (must match `deploy.yml`).
2. Enable **Required reviewers** and add yourself (and teammates later).
3. Optional: **Deployment branches** → only `main` / `master` if you want to restrict who can deploy from which branch.
4. Save.

**What happens:**

| Trigger | Flow |
|---------|------|
| **GitHub Release published** (semantic-release) | Deploy workflow starts → job waits at **Review deployments** → you approve → SSH deploy runs. |
| **Actions → Deploy → Run workflow** | Same approval gate before any SSH step. |

To approve: **Actions** → open the **Deploy** run → **Review deployments** → **Approve and deploy**.

Without required reviewers, `environment: production` only groups secrets; it does **not** pause for you.

Release and image build are **not** blocked by this gate—only the Deploy job. CI and semantic-release still run after merge; production goes live only after you approve.

### Repository secrets

| Secret | Description |
|--------|-------------|
| `EC2_HOST` | Public IP or DNS of the instance |
| `EC2_USER` | SSH user (`ubuntu` on Ubuntu AMI) |
| `EC2_SSH_KEY` | Private key (full PEM contents) for that user |
| `GHCR_PAT` | GitHub PAT with `read:packages` |

### Workflow permissions

Deploy only needs default `GITHUB_TOKEN` for checkout; pulls on EC2 use `GHCR_PAT`.

## How to deploy

### Option A — Manual (pick version)

1. Open **Actions → Deploy → Run workflow**.
2. Enter version from the GitHub Release (e.g. `1.2.0` or `v1.2.0`).
3. Workflow copies `docker-compose.yml`, pulls the image, restarts the container, hits `/api/health`.

### Option B — Automatic on GitHub Release

Publishing a **GitHub Release** (done by semantic-release) also triggers **Deploy** with that tag’s version.

To disable auto-deploy, remove the `release:` trigger from `.github/workflows/deploy.yml`.

## Rollback

Run **Deploy** again with the previous version (e.g. `1.1.0`).

## Troubleshooting

| Issue | Check |
|-------|--------|
| `docker pull` 401 | `GHCR_PAT`, package visibility, image name lowercase |
| Health check fails | `docker logs coursivo-backend`, `.env` DB URL, Neon IP allowlist |
| Connection refused | Security group, compose port binding, container running |
| Wrong version running | `docker inspect coursivo-backend --format '{{.Config.Image}}'` |

## Next steps (optional)

- **ALB + ACM** for HTTPS and proper health checks
- **Route 53** for `api.yourdomain.com`
- **SSM** instead of SSH keys
- **CloudWatch** log driver for Docker
