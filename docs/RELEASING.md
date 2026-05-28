# Releasing (Semantic Versioning)

Versions follow [SemVer](https://semver.org/): `MAJOR.MINOR.PATCH` (e.g. `1.2.3`).

## Day-to-day development

- `pom.xml` uses a **SNAPSHOT** version: `0.1.0-SNAPSHOT`
- Push to `main` → CI builds and pushes Docker tags: `latest`, git SHA
- No GitHub Release is created for normal commits

## Cut a release (automated)

1. **Bump version in `pom.xml`** (drop `-SNAPSHOT` for the release), e.g. `0.1.0`
2. Commit and push to `main` (via PR recommended)
3. **Create and push an annotated tag** matching the pom version:

   ```bash
   git tag -a v0.1.0 -m "Release 0.1.0"
   git push origin v0.1.0
   ```

4. CI on the tag will:
   - Verify `pom.xml` version matches the tag (`v0.1.0` ↔ `0.1.0`)
   - Build JAR and Docker image
   - Push to GHCR: `0.1.0`, `0.1`, `0`
   - Create a **GitHub Release** with the JAR attached

5. **Bump pom for next development** on `main`:

   ```xml
   <version>0.2.0-SNAPSHOT</version>
   ```

## Pre-releases

Tags like `v1.0.0-rc.1` are supported. Bump pom to `1.0.0-rc.1` (no SNAPSHOT) before tagging.

## Docker pull examples

```bash
# Latest main build
docker pull ghcr.io/sambhunath-sahoo/coursivo_backend:latest

# Specific release
docker pull ghcr.io/sambhunath-sahoo/coursivo_backend:0.1.0
```
