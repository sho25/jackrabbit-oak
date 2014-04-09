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
name|jcr
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
name|IOException
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|kernel
operator|.
name|KernelNodeStore
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
name|SegmentNodeStore
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
name|SegmentStore
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
name|memory
operator|.
name|MemoryStore
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

begin_comment
comment|/**  * NodeStore fixture for parametrized tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NodeStoreFixture
block|{
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|SEGMENT_MK
init|=
operator|new
name|SegmentFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|DOCUMENT_MK
init|=
operator|new
name|NodeStoreFixture
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|()
block|{
return|return
operator|new
name|CloseableNodeStore
argument_list|(
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|open
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|(
name|int
name|clusterNodeId
parameter_list|)
block|{
name|MongoConnection
name|connection
decl_stmt|;
try|try
block|{
name|connection
operator|=
operator|new
name|MongoConnection
argument_list|(
literal|"mongodb://localhost:27017/oak"
argument_list|)
expr_stmt|;
name|DB
name|mongoDB
init|=
name|connection
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|mongoDB
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
return|return
operator|new
name|CloseableNodeStore
argument_list|(
name|mk
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
if|if
condition|(
name|nodeStore
operator|instanceof
name|Closeable
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|Closeable
operator|)
name|nodeStore
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|DOCUMENT_NS
init|=
name|createDocumentFixture
argument_list|(
literal|"mongodb://localhost:27017/oak"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|DOCUMENT_JDBC
init|=
operator|new
name|NodeStoreFixture
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|()
block|{
name|String
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
literal|"jdbc:h2:mem:"
operator|+
name|id
argument_list|,
literal|"sa"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setRDBConnection
argument_list|(
name|ds
argument_list|)
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|(
name|int
name|clusterNodeId
parameter_list|)
block|{
try|try
block|{
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
literal|"jdbc:h2:mem:oaknodes-"
operator|+
name|clusterNodeId
argument_list|,
literal|"sa"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setRDBConnection
argument_list|(
name|ds
argument_list|)
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
if|if
condition|(
name|nodeStore
operator|instanceof
name|DocumentNodeStore
condition|)
block|{
operator|(
operator|(
name|DocumentNodeStore
operator|)
name|nodeStore
operator|)
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|MK_IMPL
init|=
operator|new
name|NodeStoreFixture
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|()
block|{
return|return
operator|new
name|KernelNodeStore
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{         }
block|}
decl_stmt|;
specifier|public
specifier|static
name|NodeStoreFixture
name|createDocumentFixture
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|)
block|{
return|return
operator|new
name|DocumentFixture
argument_list|(
name|uri
argument_list|)
return|;
block|}
comment|/**      * Creates a new empty {@link NodeStore} instance. An implementation must      * ensure the returned node store is indeed empty and is independent from      * instances returned from previous calls to this method.      *      * @return a new node store instance.      */
specifier|public
specifier|abstract
name|NodeStore
name|createNodeStore
parameter_list|()
function_decl|;
comment|/**      * Create a new cluster node that is attached to the same backend storage.      *       * @param clusterNodeId the cluster node id      * @return the node store, or null if clustering is not supported      */
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|(
name|int
name|clusterNodeId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
specifier|abstract
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
class|class
name|CloseableNodeStore
extends|extends
name|KernelNodeStore
implements|implements
name|Closeable
block|{
specifier|private
specifier|final
name|DocumentMK
name|kernel
decl_stmt|;
specifier|public
name|CloseableNodeStore
parameter_list|(
name|DocumentMK
name|kernel
parameter_list|)
block|{
name|super
argument_list|(
name|kernel
argument_list|)
expr_stmt|;
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|kernel
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|SegmentFixture
extends|extends
name|NodeStoreFixture
block|{
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|public
name|SegmentFixture
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentFixture
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|()
block|{
return|return
operator|new
name|SegmentNodeStore
argument_list|(
name|store
operator|==
literal|null
condition|?
operator|new
name|MemoryStore
argument_list|()
else|:
name|store
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{         }
block|}
specifier|public
specifier|static
class|class
name|DocumentFixture
extends|extends
name|NodeStoreFixture
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_URI
init|=
literal|"mongodb://localhost:27017/oak"
decl_stmt|;
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|inMemory
decl_stmt|;
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
specifier|public
name|DocumentFixture
parameter_list|(
name|String
name|uri
parameter_list|,
name|boolean
name|inMemory
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|inMemory
operator|=
name|inMemory
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
block|}
specifier|public
name|DocumentFixture
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
argument_list|(
name|uri
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DocumentFixture
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_URI
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeStore
name|createNodeStore
parameter_list|(
name|String
name|uri
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|MongoConnection
name|connection
decl_stmt|;
try|try
block|{
name|connection
operator|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|DB
name|mongoDB
init|=
name|connection
operator|.
name|getDB
argument_list|()
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
name|setMongoDB
argument_list|(
name|mongoDB
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|()
block|{
if|if
condition|(
name|inMemory
condition|)
block|{
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|createNodeStore
argument_list|(
name|uri
operator|+
literal|'-'
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|blobStore
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|(
name|int
name|clusterNodeId
parameter_list|)
block|{
return|return
name|createNodeStore
argument_list|(
name|uri
argument_list|,
name|blobStore
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
comment|// FIXME is there a better way to check whether MongoDB is available?
name|NodeStore
name|nodeStore
init|=
name|createNodeStore
argument_list|(
name|uri
argument_list|,
name|blobStore
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeStore
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|dispose
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
if|if
condition|(
name|nodeStore
operator|instanceof
name|DocumentNodeStore
condition|)
block|{
operator|(
operator|(
name|DocumentNodeStore
operator|)
name|nodeStore
operator|)
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

