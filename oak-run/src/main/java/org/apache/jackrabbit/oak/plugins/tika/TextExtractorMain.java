begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|plugins
operator|.
name|tika
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClientURI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoURI
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|aws
operator|.
name|ext
operator|.
name|ds
operator|.
name|S3DataStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|data
operator|.
name|DataStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|data
operator|.
name|DataStoreException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|data
operator|.
name|FileDataStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PropertiesUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|DataStoreTextWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|DocumentMK
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|DocumentNodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|MongoConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|TextExtractorMain
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TextExtractorMain
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|h
init|=
literal|"tika [extract|report|generate]\n"
operator|+
literal|"\n"
operator|+
literal|"report   : Generates a summary report related to binary data\n"
operator|+
literal|"extract  : Performs the text extraction\n"
operator|+
literal|"generate : Generates the csv data file based on configured NodeStore/BlobStore"
decl_stmt|;
try|try
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|help
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"?"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|nodeStoreSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"nodestore"
argument_list|,
literal|"NodeStore detail /path/to/oak/repository | mongodb://host:port/database"
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
decl_stmt|;
name|OptionSpec
name|segmentTar
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"segment-tar"
argument_list|,
literal|"Use oak-segment-tar instead of oak-segment"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|pathSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"path"
argument_list|,
literal|"Path in repository under which the binaries would be searched"
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
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|dataFileSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"data-file"
argument_list|,
literal|"Data file in csv format containing the binary metadata"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|tikaConfigSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"tika-config"
argument_list|,
literal|"Tika config file path"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|fdsDirSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"fds-path"
argument_list|,
literal|"Path of directory used by FileDataStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|s3ConfigSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"s3-config-path"
argument_list|,
literal|"Path of properties file containing config for S3DataStore"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|storeDirSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"store-path"
argument_list|,
literal|"Path of directory used to store extracted text content"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|poolSize
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"pool-size"
argument_list|,
literal|"Size of the thread pool used to perform text extraction. Defaults "
operator|+
literal|"to number of cores on the system"
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
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|nonOption
init|=
name|parser
operator|.
name|nonOptions
argument_list|(
name|h
argument_list|)
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nonOptions
init|=
name|nonOption
operator|.
name|values
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|help
argument_list|)
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nonOptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|boolean
name|report
init|=
name|nonOptions
operator|.
name|contains
argument_list|(
literal|"report"
argument_list|)
decl_stmt|;
name|boolean
name|extract
init|=
name|nonOptions
operator|.
name|contains
argument_list|(
literal|"extract"
argument_list|)
decl_stmt|;
name|boolean
name|generate
init|=
name|nonOptions
operator|.
name|contains
argument_list|(
literal|"generate"
argument_list|)
decl_stmt|;
name|File
name|dataFile
init|=
literal|null
decl_stmt|;
name|File
name|storeDir
init|=
literal|null
decl_stmt|;
name|File
name|tikaConfigFile
init|=
literal|null
decl_stmt|;
name|BlobStore
name|blobStore
init|=
literal|null
decl_stmt|;
name|BinaryResourceProvider
name|binaryResourceProvider
init|=
literal|null
decl_stmt|;
name|BinaryStats
name|stats
init|=
literal|null
decl_stmt|;
name|String
name|path
init|=
literal|"/"
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|tikaConfigSpec
argument_list|)
condition|)
block|{
name|tikaConfigFile
operator|=
name|tikaConfigSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|tikaConfigFile
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Tika config file %s does not exist"
argument_list|,
name|tikaConfigFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|storeDirSpec
argument_list|)
condition|)
block|{
name|storeDir
operator|=
name|storeDirSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
name|storeDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|checkArgument
argument_list|(
name|storeDir
operator|.
name|isDirectory
argument_list|()
argument_list|,
literal|"Path [%s] specified for storing extracted "
operator|+
literal|"text content '%s' is not a directory"
argument_list|,
name|storeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|storeDirSpec
operator|.
name|options
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|fdsDirSpec
argument_list|)
condition|)
block|{
name|File
name|fdsDir
init|=
name|fdsDirSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|fdsDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"FileDataStore %s does not exist"
argument_list|,
name|fdsDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|FileDataStore
name|fds
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setPath
argument_list|(
name|fdsDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|blobStore
operator|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|s3ConfigSpec
argument_list|)
condition|)
block|{
name|File
name|s3Config
init|=
name|s3ConfigSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|s3Config
operator|.
name|exists
argument_list|()
operator|&&
name|s3Config
operator|.
name|canRead
argument_list|()
argument_list|,
literal|"S3DataStore config cannot be read from [%s]"
argument_list|,
name|s3Config
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|loadProperties
argument_list|(
name|s3Config
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Loaded properties for S3DataStore from {}"
argument_list|,
name|s3Config
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|pathProp
init|=
literal|"path"
decl_stmt|;
name|String
name|repoPath
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|pathProp
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|repoPath
argument_list|,
literal|"Missing required property [%s] from S3DataStore config loaded from [%s]"
argument_list|,
name|pathProp
argument_list|,
name|s3Config
argument_list|)
expr_stmt|;
comment|//Check if 'secret' key is defined. It should be non null for references
comment|//to be generated. As the ref are transient we can just use any random value
comment|//if not specified
name|String
name|secretConfig
init|=
literal|"secret"
decl_stmt|;
if|if
condition|(
name|props
operator|.
name|getProperty
argument_list|(
name|secretConfig
argument_list|)
operator|==
literal|null
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|secretConfig
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Using {} for S3DataStore "
argument_list|,
name|repoPath
argument_list|)
expr_stmt|;
name|DataStore
name|ds
init|=
name|createS3DataStore
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|toMap
argument_list|(
name|props
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ds
operator|.
name|init
argument_list|(
name|pathProp
argument_list|)
expr_stmt|;
name|blobStore
operator|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|ds
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|ds
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|dataFileSpec
argument_list|)
condition|)
block|{
name|dataFile
operator|=
name|dataFileSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
name|checkNotNull
argument_list|(
name|dataFile
argument_list|,
literal|"Data file not configured with %s"
argument_list|,
name|dataFileSpec
argument_list|)
expr_stmt|;
if|if
condition|(
name|report
operator|||
name|extract
condition|)
block|{
name|checkArgument
argument_list|(
name|dataFile
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Data file %s does not exist"
argument_list|,
name|dataFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|binaryResourceProvider
operator|=
operator|new
name|CSVFileBinaryResourceProvider
argument_list|(
name|dataFile
argument_list|,
name|blobStore
argument_list|)
expr_stmt|;
if|if
condition|(
name|binaryResourceProvider
operator|instanceof
name|Closeable
condition|)
block|{
name|closer
operator|.
name|register
argument_list|(
operator|(
name|Closeable
operator|)
name|binaryResourceProvider
argument_list|)
expr_stmt|;
block|}
name|stats
operator|=
operator|new
name|BinaryStats
argument_list|(
name|tikaConfigFile
argument_list|,
name|binaryResourceProvider
argument_list|)
expr_stmt|;
name|String
name|summary
init|=
name|stats
operator|.
name|getSummary
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|summary
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|generate
condition|)
block|{
name|String
name|src
init|=
name|nodeStoreSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|blobStore
argument_list|,
literal|"BlobStore found to be null. FileDataStore directory "
operator|+
literal|"must be specified via %s"
argument_list|,
name|fdsDirSpec
operator|.
name|options
argument_list|()
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|dataFile
argument_list|,
literal|"Data file path not provided"
argument_list|)
expr_stmt|;
name|NodeStore
name|nodeStore
init|=
name|bootStrapNodeStore
argument_list|(
name|src
argument_list|,
name|options
operator|.
name|has
argument_list|(
name|segmentTar
argument_list|)
argument_list|,
name|blobStore
argument_list|,
name|closer
argument_list|)
decl_stmt|;
name|BinaryResourceProvider
name|brp
init|=
operator|new
name|NodeStoreBinaryResourceProvider
argument_list|(
name|nodeStore
argument_list|,
name|blobStore
argument_list|)
decl_stmt|;
name|CSVFileGenerator
name|generator
init|=
operator|new
name|CSVFileGenerator
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|generator
operator|.
name|generate
argument_list|(
name|brp
operator|.
name|getBinaries
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|extract
condition|)
block|{
name|checkNotNull
argument_list|(
name|storeDir
argument_list|,
literal|"Directory to store extracted text content "
operator|+
literal|"must be specified via %s"
argument_list|,
name|storeDirSpec
operator|.
name|options
argument_list|()
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|blobStore
argument_list|,
literal|"BlobStore found to be null. FileDataStore directory "
operator|+
literal|"must be specified via %s"
argument_list|,
name|fdsDirSpec
operator|.
name|options
argument_list|()
argument_list|)
expr_stmt|;
name|DataStoreTextWriter
name|writer
init|=
operator|new
name|DataStoreTextWriter
argument_list|(
name|storeDir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TextExtractor
name|extractor
init|=
operator|new
name|TextExtractor
argument_list|(
name|writer
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|poolSize
argument_list|)
condition|)
block|{
name|extractor
operator|.
name|setThreadPoolSize
argument_list|(
name|poolSize
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tikaConfigFile
operator|!=
literal|null
condition|)
block|{
name|extractor
operator|.
name|setTikaConfig
argument_list|(
name|tikaConfigFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|pathSpec
argument_list|)
condition|)
block|{
name|path
operator|=
name|pathSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
name|closer
operator|.
name|register
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|extractor
argument_list|)
expr_stmt|;
name|extractor
operator|.
name|setStats
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Using path {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|extractor
operator|.
name|extract
argument_list|(
name|binaryResourceProvider
operator|.
name|getBinaries
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|extractor
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|closer
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|toMap
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|name
range|:
name|properties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|private
specifier|static
name|DataStore
name|createS3DataStore
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|IOException
block|{
name|S3DataStore
name|s3ds
init|=
operator|new
name|S3DataStore
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
return|return
name|s3ds
return|;
block|}
specifier|private
specifier|static
name|Properties
name|loadProperties
parameter_list|(
name|File
name|s3Config
parameter_list|)
throws|throws
name|IOException
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
name|FileUtils
operator|.
name|openInputStream
argument_list|(
name|s3Config
argument_list|)
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
specifier|private
specifier|static
name|NodeStore
name|bootStrapNodeStore
parameter_list|(
name|String
name|src
parameter_list|,
name|boolean
name|segmentTar
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|src
operator|.
name|startsWith
argument_list|(
name|MongoURI
operator|.
name|MONGODB_PREFIX
argument_list|)
condition|)
block|{
name|MongoClientURI
name|uri
init|=
operator|new
name|MongoClientURI
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getDatabase
argument_list|()
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Database missing in MongoDB URI: "
operator|+
name|uri
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|MongoConnection
name|mongo
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|mongo
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentNodeStore
name|store
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
if|if
condition|(
name|segmentTar
condition|)
block|{
return|return
name|SegmentTarUtils
operator|.
name|bootstrap
argument_list|(
name|src
argument_list|,
name|blobStore
argument_list|,
name|closer
argument_list|)
return|;
block|}
return|return
name|SegmentUtils
operator|.
name|bootstrap
argument_list|(
name|src
argument_list|,
name|blobStore
argument_list|,
name|closer
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|FileStore
name|fs
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|DataStore
name|ds
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|DocumentNodeStore
name|dns
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|dns
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|MongoConnection
name|con
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

