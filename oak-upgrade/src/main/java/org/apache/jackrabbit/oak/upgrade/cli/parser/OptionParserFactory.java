begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|parser
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_class
specifier|public
class|class
name|OptionParserFactory
block|{
specifier|public
specifier|static
specifier|final
name|String
name|COPY_BINARIES
init|=
literal|"copy-binaries"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DISABLE_MMAP
init|=
literal|"disable-mmap"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_ON_ERROR
init|=
literal|"fail-on-error"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|IGNORE_MISSING_BINARIES
init|=
literal|"ignore-missing-binaries"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EARLY_SHUTDOWN
init|=
literal|"early-shutdown"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_SIZE
init|=
literal|"cache"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HELP
init|=
literal|"help"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DST_USER
init|=
literal|"user"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DST_PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_USER
init|=
literal|"src-user"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_PASSWORD
init|=
literal|"src-password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_FBS
init|=
literal|"src-fileblobstore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_FDS
init|=
literal|"src-datastore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_S3
init|=
literal|"src-s3datastore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_S3_CONFIG
init|=
literal|"src-s3config"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_AZURE
init|=
literal|"src-azuredatastore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_AZURE_CONFIG
init|=
literal|"src-azureconfig"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SRC_EXTERNAL_BLOBS
init|=
literal|"src-external-ds"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DST_FDS
init|=
literal|"datastore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DST_FBS
init|=
literal|"fileblobstore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DST_AZURE
init|=
literal|"azuredatastore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DST_AZURE_CONFIG
init|=
literal|"azureconfig"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DST_S3
init|=
literal|"s3datastore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DST_S3_CONFIG
init|=
literal|"s3config"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COPY_VERSIONS
init|=
literal|"copy-versions"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COPY_ORPHANED_VERSIONS
init|=
literal|"copy-orphaned-versions"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INCLUDE_PATHS
init|=
literal|"include-paths"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXCLUDE_PATHS
init|=
literal|"exclude-paths"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MERGE_PATHS
init|=
literal|"merge-paths"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SKIP_INIT
init|=
literal|"skip-init"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SKIP_NAME_CHECK
init|=
literal|"skip-name-check"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|VERIFY
init|=
literal|"verify"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ONLY_VERIFY
init|=
literal|"only-verify"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SKIP_CHECKPOINTS
init|=
literal|"skip-checkpoints"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FORCE_CHECKPOINTS
init|=
literal|"force-checkpoints"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ADD_SECONDARY_METADATA
init|=
literal|"add-secondary-metadata"
decl_stmt|;
specifier|public
specifier|static
name|OptionParser
name|create
parameter_list|()
block|{
name|OptionParser
name|op
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|addUsageOptions
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|addBlobOptions
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|addRdbOptions
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|addPathsOptions
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|addVersioningOptions
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|addMiscOptions
argument_list|(
name|op
argument_list|)
expr_stmt|;
return|return
name|op
return|;
block|}
specifier|private
specifier|static
name|void
name|addUsageOptions
parameter_list|(
name|OptionParser
name|op
parameter_list|)
block|{
name|op
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"?"
argument_list|,
name|HELP
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addBlobOptions
parameter_list|(
name|OptionParser
name|op
parameter_list|)
block|{
name|op
operator|.
name|accepts
argument_list|(
name|COPY_BINARIES
argument_list|,
literal|"Copy binary content. Use this to disable use of existing DataStore in new repo"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SRC_FDS
argument_list|,
literal|"Datastore directory to be used as a source FileDataStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SRC_FBS
argument_list|,
literal|"Datastore directory to be used as a source FileBlobStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SRC_S3
argument_list|,
literal|"Datastore directory to be used for the source S3"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SRC_S3_CONFIG
argument_list|,
literal|"Configuration file for the source S3DataStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SRC_AZURE
argument_list|,
literal|"Datastore directory to be used for the source Azure"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SRC_AZURE_CONFIG
argument_list|,
literal|"Configuration file for the source AzureDataStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|DST_FDS
argument_list|,
literal|"Datastore directory to be used as a target FileDataStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|DST_FBS
argument_list|,
literal|"Datastore directory to be used as a target FileBlobStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|DST_S3
argument_list|,
literal|"Datastore directory to be used for the target S3"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|DST_S3_CONFIG
argument_list|,
literal|"Configuration file for the target S3DataStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|DST_AZURE
argument_list|,
literal|"Datastore directory to be used for the target Azure"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|DST_AZURE_CONFIG
argument_list|,
literal|"Configuration file for the target AzureBlobStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|IGNORE_MISSING_BINARIES
argument_list|,
literal|"Don't break the migration if some binaries are missing"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SRC_EXTERNAL_BLOBS
argument_list|,
literal|"Flag specifying if the source Store has external references or not"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addRdbOptions
parameter_list|(
name|OptionParser
name|op
parameter_list|)
block|{
name|op
operator|.
name|accepts
argument_list|(
name|SRC_USER
argument_list|,
literal|"Source rdb user"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SRC_PASSWORD
argument_list|,
literal|"Source rdb password"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|DST_USER
argument_list|,
literal|"Target rdb user"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|DST_PASSWORD
argument_list|,
literal|"Target rdb password"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addPathsOptions
parameter_list|(
name|OptionParser
name|op
parameter_list|)
block|{
name|op
operator|.
name|accepts
argument_list|(
name|INCLUDE_PATHS
argument_list|,
literal|"Comma-separated list of paths to include during copy."
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|EXCLUDE_PATHS
argument_list|,
literal|"Comma-separated list of paths to exclude during copy."
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|MERGE_PATHS
argument_list|,
literal|"Comma-separated list of paths to merge during copy."
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addVersioningOptions
parameter_list|(
name|OptionParser
name|op
parameter_list|)
block|{
name|op
operator|.
name|accepts
argument_list|(
name|COPY_VERSIONS
argument_list|,
literal|"Copy the version storage. Parameters: { true | false | yyyy-mm-dd }. Defaults to true."
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|COPY_ORPHANED_VERSIONS
argument_list|,
literal|"Allows to skip copying orphaned versions. Parameters: { true | false | yyyy-mm-dd }. Defaults to true."
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addMiscOptions
parameter_list|(
name|OptionParser
name|op
parameter_list|)
block|{
name|op
operator|.
name|accepts
argument_list|(
name|DISABLE_MMAP
argument_list|,
literal|"Disable memory mapped file access for Segment Store"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|FAIL_ON_ERROR
argument_list|,
literal|"Fail completely if nodes can't be read from the source repo"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|EARLY_SHUTDOWN
argument_list|,
literal|"Shutdown the source repository after nodes are copied and before the commit hooks are applied"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|CACHE_SIZE
argument_list|,
literal|"Cache size in MB"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|256
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SKIP_INIT
argument_list|,
literal|"Skip the repository initialization; only copy data"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SKIP_NAME_CHECK
argument_list|,
literal|"Skip the initial phase of testing node name lengths"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|VERIFY
argument_list|,
literal|"After the sidegrade check whether the source repository is exactly the same as destination"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|ONLY_VERIFY
argument_list|,
literal|"Performs only --"
operator|+
name|VERIFY
operator|+
literal|", without copying content"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|SKIP_CHECKPOINTS
argument_list|,
literal|"Don't copy checkpoints on the full segment->segment migration"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|FORCE_CHECKPOINTS
argument_list|,
literal|"Copy checkpoints even if the --include,exclude,merge-paths option is specified"
argument_list|)
expr_stmt|;
name|op
operator|.
name|accepts
argument_list|(
name|ADD_SECONDARY_METADATA
argument_list|,
literal|"Adds the metadata required by secondary store"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

