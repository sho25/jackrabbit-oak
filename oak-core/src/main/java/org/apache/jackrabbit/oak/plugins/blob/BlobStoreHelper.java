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
name|blob
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
name|plugins
operator|.
name|blob
operator|.
name|cloud
operator|.
name|CloudBlobStore
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
name|cloud
operator|.
name|CloudBlobStoreBuilder
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
name|DataStoreBlobStoreBuilder
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
name|base
operator|.
name|Optional
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
name|base
operator|.
name|Strings
import|;
end_import

begin_comment
comment|/**  * A factory helper for creating BlobStore objects.  */
end_comment

begin_class
specifier|public
class|class
name|BlobStoreHelper
block|{
comment|/**      * Creates the appropriate BlobStoreBuilder instance based on the blobType.      *       * @param blobStoreType      *            the blob type      * @return the BlobStoreBuilder wrapped as {@link Optional} to indicate that      *         the builder returned may be null in the case of a default      *         BlobStoreType      * @throws Exception      *             the exception      */
specifier|public
specifier|static
name|Optional
argument_list|<
name|BlobStoreBuilder
argument_list|>
name|createFactory
parameter_list|(
name|BlobStoreConfiguration
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|BlobStoreBuilder
name|builder
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|config
operator|.
name|getProperty
argument_list|(
name|BlobStoreConfiguration
operator|.
name|PROP_BLOB_STORE_PROVIDER
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|blobStoreProvider
init|=
name|config
operator|.
name|getProperty
argument_list|(
name|BlobStoreConfiguration
operator|.
name|PROP_BLOB_STORE_PROVIDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobStoreProvider
operator|.
name|equals
argument_list|(
name|CloudBlobStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|=
name|CloudBlobStoreBuilder
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|blobStoreProvider
operator|.
name|equals
argument_list|(
name|DataStoreBlobStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|=
name|DataStoreBlobStoreBuilder
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|Optional
operator|.
name|fromNullable
argument_list|(
name|builder
argument_list|)
return|;
block|}
comment|/**      * Creates the appropriate BlobStore instance based on the blobType and the      * configuration.      *       * @param blobStoreType      *            the blob type      * @param config      *            the config      * @return the BlobStoreBuilder wrapped as {@link Optional} to indicate that      *         the builder returned may be null in the case of a default      *         BlobStoreType or an invalid config      * @throws Exception      *             the exception      */
specifier|public
specifier|static
name|Optional
argument_list|<
name|BlobStore
argument_list|>
name|create
parameter_list|(
name|BlobStoreConfiguration
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|BlobStore
name|blobStore
init|=
literal|null
decl_stmt|;
name|BlobStoreBuilder
name|builder
init|=
name|createFactory
argument_list|(
name|config
argument_list|)
operator|.
name|orNull
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|builder
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|config
operator|!=
literal|null
operator|)
condition|)
block|{
name|blobStore
operator|=
name|builder
operator|.
name|build
argument_list|(
name|config
argument_list|)
operator|.
name|orNull
argument_list|()
expr_stmt|;
block|}
return|return
name|Optional
operator|.
name|fromNullable
argument_list|(
name|blobStore
argument_list|)
return|;
block|}
block|}
end_class

end_unit

