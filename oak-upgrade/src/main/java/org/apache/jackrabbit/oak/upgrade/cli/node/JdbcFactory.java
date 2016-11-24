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
name|node
package|;
end_package

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
name|rdb
operator|.
name|RDBBlobStore
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
name|rdb
operator|.
name|RDBDataSourceFactory
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|JdbcFactory
implements|implements
name|NodeStoreFactory
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
name|JdbcFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|jdbcUri
decl_stmt|;
specifier|private
specifier|final
name|int
name|cacheSize
decl_stmt|;
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|readOnly
decl_stmt|;
specifier|public
name|JdbcFactory
parameter_list|(
name|String
name|jdbcUri
parameter_list|,
name|int
name|cacheSize
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
block|{
name|this
operator|.
name|jdbcUri
operator|=
name|jdbcUri
expr_stmt|;
name|this
operator|.
name|cacheSize
operator|=
name|cacheSize
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
operator|||
name|password
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"RBD requires username and password parameters."
argument_list|)
throw|;
block|}
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|readOnly
operator|=
name|readOnly
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|create
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|,
name|Closer
name|closer
parameter_list|)
block|{
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
name|MongoFactory
operator|.
name|getBuilder
argument_list|(
name|cacheSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setRDBConnection
argument_list|(
name|getDataSource
argument_list|(
name|closer
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|readOnly
condition|)
block|{
name|builder
operator|.
name|setReadOnlyMode
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Initialized DocumentNodeStore on RDB with Cache size : {} MB, Fast migration : {}"
argument_list|,
name|cacheSize
argument_list|,
name|builder
operator|.
name|isDisableBranches
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentNodeStore
name|documentNodeStore
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
name|MongoFactory
operator|.
name|asCloseable
argument_list|(
name|documentNodeStore
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|documentNodeStore
return|;
block|}
specifier|private
name|DataSource
name|getDataSource
parameter_list|(
name|Closer
name|closer
parameter_list|)
block|{
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|jdbcUri
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
if|if
condition|(
name|ds
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
name|ds
argument_list|)
expr_stmt|;
block|}
return|return
name|ds
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasExternalBlobReferences
parameter_list|()
throws|throws
name|IOException
block|{
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
name|DataSource
name|ds
init|=
name|getDataSource
argument_list|(
name|closer
argument_list|)
decl_stmt|;
name|RDBBlobStore
name|blobStore
init|=
operator|new
name|RDBBlobStore
argument_list|(
name|ds
argument_list|)
decl_stmt|;
return|return
operator|!
name|blobStore
operator|.
name|getAllChunkIds
argument_list|(
literal|0
argument_list|)
operator|.
name|hasNext
argument_list|()
return|;
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
literal|"DocumentNodeStore[%s]"
argument_list|,
name|jdbcUri
argument_list|)
return|;
block|}
block|}
end_class

end_unit

