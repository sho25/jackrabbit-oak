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
name|persistentCache
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
name|LocalDiffCache
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
name|DocumentNodeState
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
name|NodeDocument
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
name|PathRev
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
name|StringValue
import|;
end_import

begin_enum
specifier|public
enum|enum
name|CacheType
block|{
name|NODE
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|String
name|keyToString
parameter_list|(
name|K
name|key
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PathRev
operator|)
name|key
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|K
name|keyFromString
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|(
name|K
operator|)
name|PathRev
operator|.
name|fromString
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|int
name|compareKeys
parameter_list|(
name|K
name|a
parameter_list|,
name|K
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PathRev
operator|)
name|a
operator|)
operator|.
name|compareTo
argument_list|(
operator|(
name|PathRev
operator|)
name|b
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|String
name|valueToString
parameter_list|(
name|V
name|value
parameter_list|)
block|{
return|return
operator|(
operator|(
name|DocumentNodeState
operator|)
name|value
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|valueFromString
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|(
name|V
operator|)
name|DocumentNodeState
operator|.
name|fromString
argument_list|(
name|store
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
block|,
name|CHILDREN
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|String
name|keyToString
parameter_list|(
name|K
name|key
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PathRev
operator|)
name|key
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|K
name|keyFromString
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|(
name|K
operator|)
name|PathRev
operator|.
name|fromString
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|int
name|compareKeys
parameter_list|(
name|K
name|a
parameter_list|,
name|K
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PathRev
operator|)
name|a
operator|)
operator|.
name|compareTo
argument_list|(
operator|(
name|PathRev
operator|)
name|b
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|String
name|valueToString
parameter_list|(
name|V
name|value
parameter_list|)
block|{
return|return
operator|(
operator|(
name|DocumentNodeState
operator|.
name|Children
operator|)
name|value
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|valueFromString
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|(
name|V
operator|)
name|DocumentNodeState
operator|.
name|Children
operator|.
name|fromString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
block|,
name|DIFF
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|String
name|keyToString
parameter_list|(
name|K
name|key
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PathRev
operator|)
name|key
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|K
name|keyFromString
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|(
name|K
operator|)
name|PathRev
operator|.
name|fromString
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|int
name|compareKeys
parameter_list|(
name|K
name|a
parameter_list|,
name|K
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|PathRev
operator|)
name|a
operator|)
operator|.
name|compareTo
argument_list|(
operator|(
name|PathRev
operator|)
name|b
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|String
name|valueToString
parameter_list|(
name|V
name|value
parameter_list|)
block|{
return|return
operator|(
operator|(
name|StringValue
operator|)
name|value
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|valueFromString
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|(
name|V
operator|)
name|StringValue
operator|.
name|fromString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
block|,
name|CONSOLIDATED_DIFF
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|String
name|keyToString
parameter_list|(
name|K
name|key
parameter_list|)
block|{
return|return
operator|(
operator|(
name|StringValue
operator|)
name|key
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|K
name|keyFromString
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|(
name|K
operator|)
name|StringValue
operator|.
name|fromString
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|int
name|compareKeys
parameter_list|(
name|K
name|a
parameter_list|,
name|K
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|StringValue
operator|)
name|a
operator|)
operator|.
name|asString
argument_list|()
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|StringValue
operator|)
name|b
operator|)
operator|.
name|asString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|String
name|valueToString
parameter_list|(
name|V
name|value
parameter_list|)
block|{
return|return
operator|(
operator|(
name|LocalDiffCache
operator|.
name|ConsolidatedDiff
operator|)
name|value
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|valueFromString
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|(
name|V
operator|)
name|LocalDiffCache
operator|.
name|ConsolidatedDiff
operator|.
name|fromString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
block|,
name|DOC_CHILDREN
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|String
name|keyToString
parameter_list|(
name|K
name|key
parameter_list|)
block|{
return|return
operator|(
operator|(
name|StringValue
operator|)
name|key
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|K
name|keyFromString
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|(
name|K
operator|)
name|StringValue
operator|.
name|fromString
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|int
name|compareKeys
parameter_list|(
name|K
name|a
parameter_list|,
name|K
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|StringValue
operator|)
name|a
operator|)
operator|.
name|asString
argument_list|()
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|StringValue
operator|)
name|b
operator|)
operator|.
name|asString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|String
name|valueToString
parameter_list|(
name|V
name|value
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeDocument
operator|.
name|Children
operator|)
name|value
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|valueFromString
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|(
name|V
operator|)
name|NodeDocument
operator|.
name|Children
operator|.
name|fromString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
block|,
name|DOCUMENT
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|String
name|keyToString
parameter_list|(
name|K
name|key
parameter_list|)
block|{
return|return
operator|(
operator|(
name|StringValue
operator|)
name|key
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|K
name|keyFromString
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|(
name|K
operator|)
name|StringValue
operator|.
name|fromString
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
parameter_list|>
name|int
name|compareKeys
parameter_list|(
name|K
name|a
parameter_list|,
name|K
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|StringValue
operator|)
name|a
operator|)
operator|.
name|asString
argument_list|()
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|StringValue
operator|)
name|b
operator|)
operator|.
name|asString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|String
name|valueToString
parameter_list|(
name|V
name|value
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeDocument
operator|)
name|value
operator|)
operator|.
name|asString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|V
name|valueFromString
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|(
name|V
operator|)
name|NodeDocument
operator|.
name|fromString
argument_list|(
name|docStore
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
block|;
specifier|public
specifier|abstract
parameter_list|<
name|K
parameter_list|>
name|String
name|keyToString
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
parameter_list|<
name|K
parameter_list|>
name|K
name|keyFromString
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
parameter_list|<
name|K
parameter_list|>
name|int
name|compareKeys
parameter_list|(
name|K
name|a
parameter_list|,
name|K
name|b
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
parameter_list|<
name|V
parameter_list|>
name|String
name|valueToString
parameter_list|(
name|V
name|value
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
parameter_list|<
name|V
parameter_list|>
name|V
name|valueFromString
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
block|}
end_enum

end_unit

