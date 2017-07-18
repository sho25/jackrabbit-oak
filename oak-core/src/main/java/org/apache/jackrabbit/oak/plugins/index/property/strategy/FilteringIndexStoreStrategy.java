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
name|index
operator|.
name|property
operator|.
name|strategy
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|api
operator|.
name|CommitFailedException
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
name|query
operator|.
name|Filter
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
name|NodeBuilder
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
name|NodeState
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
name|Predicate
import|;
end_import

begin_comment
comment|/**  * A delegating IndexStoreStrategy that filters out updates that are not  * accepted by the given predicate  */
end_comment

begin_class
specifier|public
class|class
name|FilteringIndexStoreStrategy
implements|implements
name|IndexStoreStrategy
block|{
specifier|private
specifier|final
name|IndexStoreStrategy
name|strategy
decl_stmt|;
specifier|private
specifier|final
name|Predicate
argument_list|<
name|String
argument_list|>
name|filter
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|readOnly
decl_stmt|;
specifier|public
name|FilteringIndexStoreStrategy
parameter_list|(
name|IndexStoreStrategy
name|strategy
parameter_list|,
name|Predicate
argument_list|<
name|String
argument_list|>
name|filter
parameter_list|)
block|{
name|this
argument_list|(
name|strategy
argument_list|,
name|filter
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FilteringIndexStoreStrategy
parameter_list|(
name|IndexStoreStrategy
name|strategy
parameter_list|,
name|Predicate
argument_list|<
name|String
argument_list|>
name|filter
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
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
name|void
name|update
parameter_list|(
name|Supplier
argument_list|<
name|NodeBuilder
argument_list|>
name|index
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|indexName
parameter_list|,
name|NodeBuilder
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|beforeKeys
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|afterKeys
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|filter
operator|.
name|apply
argument_list|(
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|readOnly
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|UNSUPPORTED
argument_list|,
literal|0
argument_list|,
literal|"Unsupported commit to a read-only store!"
argument_list|,
operator|new
name|Throwable
argument_list|(
literal|"Commit path: "
operator|+
name|path
argument_list|)
argument_list|)
throw|;
block|}
name|strategy
operator|.
name|update
argument_list|(
name|index
argument_list|,
name|path
argument_list|,
name|indexName
argument_list|,
name|indexMeta
argument_list|,
name|beforeKeys
argument_list|,
name|afterKeys
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|Supplier
argument_list|<
name|NodeBuilder
argument_list|>
name|index
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
name|strategy
operator|.
name|exists
argument_list|(
name|index
argument_list|,
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|indexName
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
name|strategy
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|indexName
argument_list|,
name|indexMeta
argument_list|,
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|count
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|int
name|max
parameter_list|)
block|{
return|return
name|strategy
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
argument_list|,
name|values
argument_list|,
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|count
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|int
name|max
parameter_list|)
block|{
return|return
name|strategy
operator|.
name|count
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
name|indexMeta
argument_list|,
name|values
argument_list|,
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexNodeName
parameter_list|()
block|{
return|return
name|strategy
operator|.
name|getIndexNodeName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

