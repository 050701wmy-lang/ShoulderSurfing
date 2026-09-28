# Unified Camera migration (Fabric 26.2)

This fork tracks `Exopandora/ShoulderSurfing` on its `26.2` branch. The `master` branch targets a newer Minecraft release. Keep the original `LICENSE` and upstream history when syncing.

## Git layout

- `upstream`: `https://github.com/Exopandora/ShoulderSurfing.git`
- `origin`: `https://github.com/050701wmy-lang/ShoulderSurfing.git`
- `unifiedcamera-26.2`: custom work based on upstream `26.2`

For a later update, fetch both remotes and merge `upstream/26.2` into `unifiedcamera-26.2`. Review and test the merge before pushing. Do not merge `upstream/master` into a 26.2 release branch.

## Migration status

The previous standalone Unified Camera 0.1.23 project remains in the parent directory. Its code and `config/unifiedcamera.json` are retained as a reference. This fork uses Shoulder Surfing Reloaded's mod ID and `shouldersurfing-client.cfg`; it does not import the old JSON automatically.

Already provided by upstream 5.1.1: shoulder offsets and presets, free look, decoupled camera, perspective controls, adaptive and dynamic crosshair, player transparency, configurable collision, drag and sway, temporary first person in constrained spaces, and Chinese translations.

Migrated here: optional Wynntils 5 cutscene detection through the upstream temporary-first-person event. The setting is under Integrations → Wynntils. When the local avatar fades, its model, armor, and cape use the translucent terrain depth target and submit after terrain, carrying over the ice-occlusion fix from the standalone mod.

Still to reconcile with the previous implementation and verify in game: fixed-crosshair projectile alignment; the migrated transparent player depth behind ice; smooth continuous camera distance controls; shoulder-side selection when mounting; transitions when entering and leaving a mount; automatic perspective by vehicle type; detached camera and zoom; and the precise constrained-space transition preferred by the user. Equivalent upstream behavior must be verified before any old implementation is ported.

## Testing and installation

Build the Fabric module with `./gradlew :fabric:build`. The fork requires Fabric API and Forge Config API Port on Minecraft 26.2. Use only one camera mod during tests: remove the standalone `unifiedcamera-*.jar` and any official ShoulderSurfing jar before installing this fork's Fabric jar. Preserve the old jar and config so they can be restored if the migration needs rollback. The user performs in-game testing.

The first build on this machine has not completed because Java's TLS connection to external Gradle repositories terminates during dependency downloads. GitHub Actions is enabled on the fork for build verification. No installable fork jar has been verified yet.
