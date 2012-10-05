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
name|index
operator|.
name|property
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
name|Map
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
name|jcr
operator|.
name|PropertyType
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
name|Preconditions
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
name|Lists
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
name|api
operator|.
name|CoreValue
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
name|PropertyState
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
name|memory
operator|.
name|CoreValues
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
name|memory
operator|.
name|PropertyStates
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
name|memory
operator|.
name|StringValue
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

begin_class
class|class
name|PropertyIndexUpdate
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|node
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|insert
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|remove
decl_stmt|;
specifier|public
name|PropertyIndexUpdate
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|node
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|insert
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|remove
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|insert
parameter_list|(
name|String
name|path
parameter_list|,
name|Iterable
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|putValues
argument_list|(
name|insert
argument_list|,
name|path
operator|.
name|substring
argument_list|(
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|,
name|Iterable
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|putValues
argument_list|(
name|remove
argument_list|,
name|path
operator|.
name|substring
argument_list|(
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|putValues
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|map
parameter_list|,
name|String
name|path
parameter_list|,
name|Iterable
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
for|for
control|(
name|CoreValue
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|!=
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
name|String
name|key
init|=
name|PropertyIndex
operator|.
name|encode
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|==
literal|null
condition|)
block|{
name|paths
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|apply
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|boolean
name|unique
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"unique"
argument_list|)
operator|!=
literal|null
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|node
operator|.
name|child
argument_list|(
literal|":index"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|remove
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|encoded
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|PropertyState
name|property
init|=
name|index
operator|.
name|getProperty
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|CoreValues
operator|.
name|getValues
argument_list|(
name|property
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|paths
operator|.
name|contains
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|index
operator|.
name|removeProperty
argument_list|(
name|encoded
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|encoded
argument_list|,
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|insert
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|encoded
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|PropertyState
name|property
init|=
name|index
operator|.
name|getProperty
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CoreValue
name|value
range|:
name|CoreValues
operator|.
name|getValues
argument_list|(
name|property
argument_list|)
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|paths
operator|.
name|remove
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|values
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|index
operator|.
name|removeProperty
argument_list|(
name|encoded
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|unique
operator|&&
name|values
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Uniqueness constraint violated"
argument_list|)
throw|;
block|}
else|else
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|encoded
argument_list|,
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

