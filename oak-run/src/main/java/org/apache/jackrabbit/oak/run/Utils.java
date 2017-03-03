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
name|run
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
import|import static
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
operator|.
name|populate
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
name|FileInputStream
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
name|util
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|cm
operator|.
name|file
operator|.
name|ConfigurationHandler
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
name|oak
operator|.
name|blob
operator|.
name|cloud
operator|.
name|aws
operator|.
name|s3
operator|.
name|SharedS3DataStore
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
name|OakFileDataStore
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
name|spi
operator|.
name|blob
operator|.
name|GarbageCollectableBlobStore
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
name|ArgumentAcceptingOptionSpec
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

begin_class
class|class
name|Utils
block|{
specifier|private
specifier|static
specifier|final
name|long
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|public
specifier|static
name|NodeStore
name|bootstrapNodeStore
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Closer
name|closer
parameter_list|,
name|String
name|h
parameter_list|)
throws|throws
name|IOException
block|{
comment|//TODO add support for other NodeStore flags
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|clusterId
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"clusterId"
argument_list|,
literal|"MongoMK clusterId"
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
literal|0
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|disableBranchesSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"disableBranches"
argument_list|,
literal|"disable branches"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|cacheSizeSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"cacheSize"
argument_list|,
literal|"cache size"
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
literal|0
argument_list|)
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
name|String
name|src
init|=
name|nonOptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setMongoDB
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|disableBranchesSpec
argument_list|)
condition|)
block|{
name|builder
operator|.
name|disableBranches
argument_list|()
expr_stmt|;
block|}
name|int
name|cacheSize
init|=
name|cacheSizeSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheSize
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
operator|*
name|MB
argument_list|)
expr_stmt|;
block|}
name|DocumentNodeStore
name|store
init|=
name|builder
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
return|return
name|SegmentTarUtils
operator|.
name|bootstrapNodeStore
argument_list|(
name|src
argument_list|,
name|closer
argument_list|)
return|;
block|}
annotation|@
name|Nullable
specifier|public
specifier|static
name|GarbageCollectableBlobStore
name|bootstrapDataStore
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|allowsUnrecognizedOptions
argument_list|()
expr_stmt|;
name|ArgumentAcceptingOptionSpec
argument_list|<
name|String
argument_list|>
name|s3dsConfig
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"s3ds"
argument_list|,
literal|"S3DataStore config"
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
name|ArgumentAcceptingOptionSpec
argument_list|<
name|String
argument_list|>
name|fdsConfig
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"fds"
argument_list|,
literal|"FileDataStore config"
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
if|if
condition|(
operator|!
name|options
operator|.
name|has
argument_list|(
name|s3dsConfig
argument_list|)
operator|&&
operator|!
name|options
operator|.
name|has
argument_list|(
name|fdsConfig
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DataStore
name|delegate
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|s3dsConfig
argument_list|)
condition|)
block|{
name|SharedS3DataStore
name|s3ds
init|=
operator|new
name|SharedS3DataStore
argument_list|()
decl_stmt|;
name|String
name|cfgPath
init|=
name|s3dsConfig
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|Properties
name|props
init|=
name|loadAndTransformProps
argument_list|(
name|cfgPath
argument_list|)
decl_stmt|;
name|s3ds
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|delegate
operator|=
name|s3ds
expr_stmt|;
block|}
else|else
block|{
name|delegate
operator|=
operator|new
name|OakFileDataStore
argument_list|()
expr_stmt|;
name|String
name|cfgPath
init|=
name|fdsConfig
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|Properties
name|props
init|=
name|loadAndTransformProps
argument_list|(
name|cfgPath
argument_list|)
decl_stmt|;
name|populate
argument_list|(
name|delegate
argument_list|,
name|Maps
operator|.
name|fromProperties
argument_list|(
name|props
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|DataStoreBlobStore
name|blobStore
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|delegate
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|Utils
operator|.
name|asCloseable
argument_list|(
name|blobStore
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|blobStore
return|;
block|}
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
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|DataStoreBlobStore
name|blobStore
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
name|blobStore
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
name|Properties
name|loadAndTransformProps
parameter_list|(
name|String
name|cfgPath
parameter_list|)
throws|throws
name|IOException
block|{
name|Dictionary
name|dict
init|=
name|ConfigurationHandler
operator|.
name|read
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|cfgPath
argument_list|)
argument_list|)
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Enumeration
name|keys
init|=
name|dict
operator|.
name|keys
argument_list|()
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|keys
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|dict
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
block|}
end_class

end_unit

