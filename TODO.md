
* If revision is null in source declaration, but not null in source/test code, then that should not match (trigger a warning ...)
* If a requirement has everything that match but the revision, trigger a warning of out to date ...
* Rewrite ReqCoCo Runner so that params are retrieved from a config file and possible to treat either file or redmine source or both for aggregation
* Synchronize all collections where Stream.parallelStream() is used !
* Add the possibility to set the revision for redmine (so retrieve the revision if the @Req() tag is used and the revision specified into it ...)