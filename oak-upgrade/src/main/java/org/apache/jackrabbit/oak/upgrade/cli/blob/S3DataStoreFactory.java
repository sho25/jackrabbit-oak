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
name|blob
package|;
end_package

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
name|FileReader
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
name|HashSet
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|CachingDataStore
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
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
import|;
end_import

begin_class
specifier|public
class|class
name|S3DataStoreFactory
implements|implements
name|BlobStoreFactory
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
name|S3DataStoreFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|STRIP_VALUE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[TILFDXSCB]?\"(.*)\"\\W*$"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Properties
name|props
decl_stmt|;
specifier|private
specifier|final
name|String
name|directory
decl_stmt|;
specifier|private
specifier|final
name|File
name|tempHomeDir
decl_stmt|;
specifier|public
name|S3DataStoreFactory
parameter_list|(
name|String
name|configuration
parameter_list|,
name|String
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|FileReader
name|reader
init|=
operator|new
name|FileReader
argument_list|(
operator|new
name|File
argument_list|(
name|configuration
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Object
name|key
range|:
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|(
name|props
operator|.
name|keySet
argument_list|()
argument_list|)
control|)
block|{
name|String
name|value
init|=
name|props
operator|.
name|getProperty
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|stripValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|tempHomeDir
operator|=
name|Files
operator|.
name|createTempDir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|BlobStore
name|create
parameter_list|(
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
name|S3DataStore
name|delegate
init|=
operator|new
name|S3DataStore
argument_list|()
decl_stmt|;
name|delegate
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|setPath
argument_list|(
name|directory
argument_list|)
expr_stmt|;
try|try
block|{
name|delegate
operator|.
name|init
argument_list|(
name|tempHomeDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
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
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|delegate
argument_list|,
name|tempHomeDir
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|DataStoreBlobStore
argument_list|(
name|delegate
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|CachingDataStore
name|store
parameter_list|,
specifier|final
name|File
name|tempHomeDir
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
while|while
condition|(
operator|!
name|store
operator|.
name|getPendingUploads
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Waiting for following uploads to finish: "
operator|+
name|store
operator|.
name|getPendingUploads
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tempHomeDir
argument_list|)
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
catch|catch
parameter_list|(
name|InterruptedException
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
specifier|static
name|String
name|stripValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|STRIP_VALUE_PATTERN
operator|.
name|matcher
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|value
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"S3DataStore[%s]"
argument_list|,
name|directory
argument_list|)
return|;
block|}
block|}
end_class

end_unit

