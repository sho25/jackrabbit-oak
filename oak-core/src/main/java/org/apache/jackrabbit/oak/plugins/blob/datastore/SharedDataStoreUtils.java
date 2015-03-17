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
operator|.
name|datastore
package|;
end_package

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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|Joiner
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
name|Splitter
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
name|FluentIterable
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
name|Ordering
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
name|Sets
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
name|DataRecord
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
name|SharedDataStore
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

begin_comment
comment|/**  * Utility class for {@link SharedDataStore}.  */
end_comment

begin_class
specifier|public
class|class
name|SharedDataStoreUtils
block|{
comment|/**      * Checks if the blob store shared.      *      * @param blobStore the blob store      * @return true if shared      */
specifier|public
specifier|static
name|boolean
name|isShared
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|)
block|{
return|return
operator|(
name|blobStore
operator|instanceof
name|SharedDataStore
operator|)
operator|&&
operator|(
operator|(
operator|(
name|SharedDataStore
operator|)
name|blobStore
operator|)
operator|.
name|getType
argument_list|()
operator|==
name|SharedDataStore
operator|.
name|Type
operator|.
name|SHARED
operator|)
return|;
block|}
comment|/**      * Gets the earliest record of the available reference records.      *       * @param recs the recs      * @return the earliest record      */
specifier|public
specifier|static
name|DataRecord
name|getEarliestRecord
parameter_list|(
name|List
argument_list|<
name|DataRecord
argument_list|>
name|recs
parameter_list|)
block|{
return|return
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|onResultOf
argument_list|(
operator|new
name|Function
argument_list|<
name|DataRecord
argument_list|,
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Long
name|apply
parameter_list|(
annotation|@
name|Nonnull
name|DataRecord
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getLastModified
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|min
argument_list|(
name|recs
argument_list|)
return|;
block|}
comment|/**      * Repositories from which marked references not available.      *       * @param repos the repos      * @param refs the refs      * @return the sets the sets whose references not available      */
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|refsNotAvailableFromRepos
parameter_list|(
name|List
argument_list|<
name|DataRecord
argument_list|>
name|repos
parameter_list|,
name|List
argument_list|<
name|DataRecord
argument_list|>
name|refs
parameter_list|)
block|{
return|return
name|Sets
operator|.
name|difference
argument_list|(
name|FluentIterable
operator|.
name|from
argument_list|(
name|repos
argument_list|)
operator|.
name|uniqueIndex
argument_list|(
operator|new
name|Function
argument_list|<
name|DataRecord
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nonnull
name|DataRecord
name|input
parameter_list|)
block|{
return|return
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getIdFromName
argument_list|(
name|input
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|,
name|FluentIterable
operator|.
name|from
argument_list|(
name|refs
argument_list|)
operator|.
name|uniqueIndex
argument_list|(
operator|new
name|Function
argument_list|<
name|DataRecord
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nonnull
name|DataRecord
name|input
parameter_list|)
block|{
return|return
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getIdFromName
argument_list|(
name|input
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Encapsulates the different type of records at the data store root.      */
specifier|public
enum|enum
name|SharedStoreRecordType
block|{
name|REFERENCES
argument_list|(
literal|"references"
argument_list|)
block|,
name|REPOSITORY
argument_list|(
literal|"repository"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
name|SharedStoreRecordType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|String
name|getIdFromName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Splitter
operator|.
name|on
argument_list|(
name|DELIIM
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|splitToList
argument_list|(
name|name
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
return|;
block|}
specifier|public
name|String
name|getNameFromId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
name|DELIIM
argument_list|)
operator|.
name|join
argument_list|(
name|getType
argument_list|()
argument_list|,
name|id
argument_list|)
return|;
block|}
specifier|static
specifier|final
name|String
name|DELIIM
init|=
literal|"-"
decl_stmt|;
block|}
block|}
end_class

end_unit

