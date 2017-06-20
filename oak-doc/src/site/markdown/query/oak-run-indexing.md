# Oak Run Indexing

`@since Oak 1.7.0`

**Work in progress. Not to be used on production setups**

With Oak 1.7 we have added some tooling as part of oak-run `index` command. Below are details around various
operations supported by this command.

The `index` command supports connecting to different NodeStores via various options which are documented 
[here](../features/oak-run-nodestore-connection-options.md). Example below assume a setup consisting of 
SegmentNodeStore and FileDataStore. Depending on setup use the appropriate connection options.

By default the tool would generate output file in directory `indexing-result` which is referred to as output directory.
 
Unless specified all operations connect to the repository in read only mode

## Common Options

All the commands support following common options

1. `--index-paths` - Comma separated list of index paths for which the selected operations need to be performed. If
   not specified then the operation would be performed against all the indexes.
   
Also refer to help output via `-h` command for some other options

## Generate Index Info

    java -jar oak-run*.jar index --fds-path=/path/to/datastore  /path/to/segmentstore/ --index-info 

Generates a report consisting of various stats related to indexes present in the given repository. The generated
report is stored by default in `<output dir>/index-info.txt`

Supported for all index types

## Dump Index Definitions

    java -jar oak-run*.jar index --fds-path=/path/to/datastore  /path/to/segmentstore/ --index-definitions
     
`--index-definitions` operation dumps the index definition in json format to a file `<output dir>/index-definitions.json`. The json
file contains index definitions keyed against the index paths

Supported for all index types

## Dump Index Data

    java -jar oak-run*.jar index --fds-path=/path/to/datastore  /path/to/segmentstore/ --index-dump
     
`--index-dump` operation dumps the index content in output directory. The output directory would contain one folder for 
each index. Each folder would have a property file `index-details.txt` which contains `indexPath`

Supported for only Lucene indexes.

## Index Consistency Check

    java -jar oak-run*.jar index --fds-path=/path/to/datastore  /path/to/segmentstore/ --index-consistency-check
    
`--index-consistency-check` operation performs index consistency check against various indexes. It supports 2 level

* Level 1 - Specified as `--index-consistency-check=1`. Performs a basic check to determine if all blobs referred in index
  are valid
* Level 2 - Specified as `--index-consistency-check=2`. Performs a more through check to determine if all index files
  are valid and no corruption has happened. This check is slower
  
It would generate a report in `<output dir>/index-consistency-check-report.txt`

Supported for only Lucene indexes.

## Reindex

The reindex operation supports 2 modes of index

* Online Indexing - Here oak-run would connect to repository in `--read-write` mode
* Out-of-band indexing - Here oak-run would connect to repository in read only mode. It would require certain manual steps

Supported for only Lucene indexes.

### out-of-band indexing

Out of band indexing has following phases

1. Get checkpoint issued 
2. Perform indexing with read only connection to NodeStore upto checkpoint state
3. Import the generated indexes and complete the increment indexing from checkpoint state to current head


#### Step 1 - Text PreExtraction

If the index being reindexed involves fulltext index and the repository has binary content then its recommended
that first  [text pre-extraction](pre-extract-text.md) is performed. This ensures that costly operation around text
extraction is done prior to actual indexing so that actual indexing does not do text extraction in critical path

#### Step 2 - Create Checkpoint

Go to `CheckpointMBean` and create a checkpoint with lifetime of 1 month. <<TBD>>

#### Step 3 - Perform Reindex

In this step we perform the actual indexing via oak-run where it connects to repository in read only mode. 
    
     java -jar oak-run*.jar index --fds-path=/path/to/datastore  /path/to/segmentstore/ --reindex --index-paths=/oak:index/indexName
     
Here following options can be used

* `--pre-extracted-text-dir` - Directory path containing pre extracted text generated via step #1
* `--index-paths` - This command requires an explicit set of index paths which need to be indexed
* `--checkpoint` - The checkpoint up to which the index is updated, when indexing in read only mode. For
  testing purpose, it can be set to 'head' to indicate that the head state should be used.




     
     
     