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
name|MongoClient
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_class
specifier|public
class|class
name|MongoFactory
implements|implements
name|NodeStoreFactory
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
specifier|private
specifier|final
name|MongoClientURI
name|uri
decl_stmt|;
specifier|private
specifier|final
name|int
name|cacheSize
decl_stmt|;
specifier|public
name|MongoFactory
parameter_list|(
name|String
name|repoDesc
parameter_list|,
name|int
name|cacheSize
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
operator|new
name|MongoClientURI
argument_list|(
name|repoDesc
argument_list|)
expr_stmt|;
name|this
operator|.
name|cacheSize
operator|=
name|cacheSize
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
throws|throws
name|UnknownHostException
block|{
name|String
name|db
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
name|db
operator|=
literal|"aem-author"
expr_stmt|;
comment|// assume an author instance
block|}
else|else
block|{
name|db
operator|=
name|uri
operator|.
name|getDatabase
argument_list|()
expr_stmt|;
block|}
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
name|getBuilder
argument_list|(
name|cacheSize
argument_list|)
decl_stmt|;
name|MongoClient
name|client
init|=
operator|new
name|MongoClient
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|client
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMongoDB
argument_list|(
name|client
operator|.
name|getDB
argument_list|(
name|db
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|DocumentNodeStore
name|documentNodeStore
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
name|documentNodeStore
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
name|MongoClient
name|client
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
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|static
name|DocumentMK
operator|.
name|Builder
name|getBuilder
parameter_list|(
name|int
name|cacheSize
parameter_list|)
block|{
name|boolean
name|fastMigration
init|=
operator|!
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"mongomk.disableFastMigration"
argument_list|)
decl_stmt|;
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
name|memoryCacheSize
argument_list|(
name|cacheSize
operator|*
name|MB
argument_list|)
expr_stmt|;
if|if
condition|(
name|fastMigration
condition|)
block|{
name|builder
operator|.
name|disableBranches
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
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
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

