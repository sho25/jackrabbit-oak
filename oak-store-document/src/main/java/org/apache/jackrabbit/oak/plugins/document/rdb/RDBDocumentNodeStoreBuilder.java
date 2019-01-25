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
name|plugins
operator|.
name|document
operator|.
name|rdb
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
name|Suppliers
operator|.
name|ofInstance
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
name|ReferencedBlob
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
name|DocumentNodeStoreBuilder
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
name|DocumentStore
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
name|MissingLastRevSeeker
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
name|VersionGCSupport
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

begin_comment
comment|/**  * A builder for a {@link DocumentNodeStore} backed by a relational database.  */
end_comment

begin_class
specifier|public
class|class
name|RDBDocumentNodeStoreBuilder
extends|extends
name|DocumentNodeStoreBuilder
argument_list|<
name|RDBDocumentNodeStoreBuilder
argument_list|>
block|{
comment|/**      * @return a new {@link RDBDocumentNodeStoreBuilder}.      */
specifier|public
specifier|static
name|RDBDocumentNodeStoreBuilder
name|newRDBDocumentNodeStoreBuilder
parameter_list|()
block|{
return|return
operator|new
name|RDBDocumentNodeStoreBuilder
argument_list|()
return|;
block|}
comment|/**      * Sets a {@link DataSource} to use for the RDB document and blob      * stores.      *      * @return this      */
specifier|public
name|RDBDocumentNodeStoreBuilder
name|setRDBConnection
parameter_list|(
name|DataSource
name|ds
parameter_list|)
block|{
name|setRDBConnection
argument_list|(
name|ds
argument_list|,
operator|new
name|RDBOptions
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|thisBuilder
argument_list|()
return|;
block|}
comment|/**      * Sets a {@link DataSource} to use for the RDB document and blob      * stores, including {@link RDBOptions}.      *      * @return this      */
specifier|public
name|RDBDocumentNodeStoreBuilder
name|setRDBConnection
parameter_list|(
name|DataSource
name|ds
parameter_list|,
name|RDBOptions
name|options
parameter_list|)
block|{
name|this
operator|.
name|documentStoreSupplier
operator|=
name|ofInstance
argument_list|(
operator|new
name|RDBDocumentStore
argument_list|(
name|ds
argument_list|,
name|this
argument_list|,
name|options
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|blobStore
operator|==
literal|null
condition|)
block|{
name|GarbageCollectableBlobStore
name|s
init|=
operator|new
name|RDBBlobStore
argument_list|(
name|ds
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|setGCBlobStore
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|thisBuilder
argument_list|()
return|;
block|}
comment|/**      * Sets a {@link DataSource}s to use for the RDB document and blob      * stores.      *      * @return this      */
specifier|public
name|RDBDocumentNodeStoreBuilder
name|setRDBConnection
parameter_list|(
name|DataSource
name|documentStoreDataSource
parameter_list|,
name|DataSource
name|blobStoreDataSource
parameter_list|)
block|{
name|this
operator|.
name|documentStoreSupplier
operator|=
name|ofInstance
argument_list|(
operator|new
name|RDBDocumentStore
argument_list|(
name|documentStoreDataSource
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|blobStore
operator|==
literal|null
condition|)
block|{
name|GarbageCollectableBlobStore
name|s
init|=
operator|new
name|RDBBlobStore
argument_list|(
name|blobStoreDataSource
argument_list|)
decl_stmt|;
name|setGCBlobStore
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|thisBuilder
argument_list|()
return|;
block|}
specifier|public
name|VersionGCSupport
name|createVersionGCSupport
parameter_list|()
block|{
name|DocumentStore
name|store
init|=
name|getDocumentStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|store
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
return|return
operator|new
name|RDBVersionGCSupport
argument_list|(
operator|(
name|RDBDocumentStore
operator|)
name|store
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createVersionGCSupport
argument_list|()
return|;
block|}
block|}
specifier|public
name|Iterable
argument_list|<
name|ReferencedBlob
argument_list|>
name|createReferencedBlobs
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|)
block|{
specifier|final
name|DocumentStore
name|store
init|=
name|getDocumentStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|store
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
return|return
parameter_list|()
lambda|->
operator|new
name|RDBBlobReferenceIterator
argument_list|(
name|ns
argument_list|,
operator|(
name|RDBDocumentStore
operator|)
name|store
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createReferencedBlobs
argument_list|(
name|ns
argument_list|)
return|;
block|}
block|}
specifier|public
name|MissingLastRevSeeker
name|createMissingLastRevSeeker
parameter_list|()
block|{
specifier|final
name|DocumentStore
name|store
init|=
name|getDocumentStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|store
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
return|return
operator|new
name|RDBMissingLastRevSeeker
argument_list|(
operator|(
name|RDBDocumentStore
operator|)
name|store
argument_list|,
name|getClock
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createMissingLastRevSeeker
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

