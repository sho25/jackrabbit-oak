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
name|segment
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
name|checkNotNull
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
name|checkState
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
name|segment
operator|.
name|MapRecord
operator|.
name|HASH_MASK
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|collect
operator|.
name|ComparisonChain
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
name|AbstractChildNodeEntry
import|;
end_import

begin_comment
comment|/**  * Representation of a single key-value entry in a map.  */
end_comment

begin_class
class|class
name|MapEntry
extends|extends
name|AbstractChildNodeEntry
implements|implements
name|Map
operator|.
name|Entry
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
implements|,
name|Comparable
argument_list|<
name|MapEntry
argument_list|>
block|{
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|RecordId
name|key
decl_stmt|;
annotation|@
name|CheckForNull
specifier|private
specifier|final
name|RecordId
name|value
decl_stmt|;
name|MapEntry
parameter_list|(
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|key
parameter_list|,
annotation|@
name|Nullable
name|RecordId
name|value
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|checkNotNull
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|checkNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|int
name|getHash
parameter_list|()
block|{
return|return
name|MapRecord
operator|.
name|getHash
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|//----------------------------------------------------< ChildNodeEntry>--
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|SegmentNodeState
name|getNodeState
parameter_list|()
block|{
name|checkState
argument_list|(
name|value
operator|!=
literal|null
argument_list|)
expr_stmt|;
return|return
name|reader
operator|.
name|readNode
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|//---------------------------------------------------------< Map.Entry>--
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordId
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|RecordId
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|RecordId
name|setValue
parameter_list|(
name|RecordId
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|//--------------------------------------------------------< Comparable>--
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
annotation|@
name|Nonnull
name|MapEntry
name|that
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compare
argument_list|(
name|getHash
argument_list|()
operator|&
name|HASH_MASK
argument_list|,
name|that
operator|.
name|getHash
argument_list|()
operator|&
name|HASH_MASK
argument_list|)
operator|.
name|compare
argument_list|(
name|name
argument_list|,
name|that
operator|.
name|name
argument_list|)
operator|.
name|compare
argument_list|(
name|value
argument_list|,
name|that
operator|.
name|value
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
block|}
end_class

end_unit

