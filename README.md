<p align="center">
<img src="assets/logo.png">
</p>

[![Carbonetes-Jacked](https://img.shields.io/badge/carbonetes-jacked-%232f7ea3)](https://github.com/carbonetes/diggity)
[![Jacked-Jenkins](https://img.shields.io/badge/diggity-jenkins--plugin-%232f7ea3)]
# Jenkins Plugin: Diggity

## Introduction
<br>

[Diggity](https://github.com/carbonetes/diggity) provides organizations with a comprehensive view of their applications to enable informed decision-making and improve security posture. Its primary purpose is to scan for bill-of-materials, licenses, and exposed secrets.

This Jenkins plugin scans a specified target and exposes its dependencies, licenses and exposed secrets.


# Plugin Configuration Fields and Descriptions
## Scan Type
<b>Description: </b>Specified the input on scan field based on the selected scan type.
<br>
<b>Option:</b>
- `Image`: Provide the image to be scanned.
- `Directory`: Provide the target directory path to be scanned.
- `Tar File`: Provide the target tar file path to be scanned.

## Scan
<b>Input: </b> Image name, Directory path, tar file path, or sbom file path.

## Skip Build Fail
Default value is `false / unchecked`.
<br>
<b>Warning:</b> If the value is checked, it will restrict the plugin from failing the build based on the assessment result.

# Usage as Pipeline
```yaml
pipeline {
    agent any
    stages {
        stage('Diggity Scan') {
            steps {
                script {
                    diggity(
                        token: '',                          // Personal access token: generated from Carbonetes Enterprise.
                        scanType: 'image',                  // Choose Scan Type: image, directory, tar, or sbom.
                        scanName: 'carbonetes/broker',      // Input: Image name, Directory path, tar file path, or sbom file path.
                        skipFail: false,                    // Default: false.
                    )
                }
            }
        }
    }
}
```
# LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

