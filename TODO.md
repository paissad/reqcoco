
* If revision is null in source declaration, but not null in source/test code, then that should not match (trigger a warning ...)
* If a requirement has everything that match but the revision, trigger a warning of out to date ...
* Synchronize all collections where Stream.parallelStream() is used !
* Add the possibility to set the revision for redmine (so retrieve the revision if the @Req() tag is used and the revision specified into it ...)
* Add the possibility to decode XML & HTML via an option if specified, while reading the source/test code and the source of declared requirements.