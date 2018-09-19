
## [Unreleased]

## [1.0.3] - 2018-09-19
### Added
 - GitHub: add support for parsing GitHub issues
 
### Fixed
 - Do not stop the requirement computation when unable to read files (issue #19) 
 

## [1.0.2] - 2017-06-20
### Fixed
 - Redmine: Now it is not compatible to use a tag & a custom field (issue #16)

## [1.0.1] - 2017-06-15
### Added
 - Runner : aggregate all reports (excel, html, ...) into a single ZIP file on top of all reports subfolders (issue #5)
 - Redmine : set requirement tag in a custom field (issue #7)
 - Maven plugin : add skip option (issue #6)
 
### Changed
 - Redmine : strip tag, if they are used, from the generated all reports (issue #4)
 - Maven plugin : set default report name to ${artifactId}-requirements-coverage (issue #2)
 - Maven plugin : add more documentation for 'sourceType' and other fields (issue #3)
 - The name of the field containing the ID of the requirements is now named 'name' rather than 'id' (for all reports)
 - Excel report : the zoom is set by default to 100% rather than 150%

## 1.0.0 - 2017-05-20
 - Initial version

[Unreleased]: https://github.com/paissad/reqcoco/compare/v1.0.3...HEAD
[1.0.3]: https://github.com/paissad/reqcoco/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/paissad/reqcoco/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/paissad/reqcoco/compare/v1.0.0...v1.0.1
