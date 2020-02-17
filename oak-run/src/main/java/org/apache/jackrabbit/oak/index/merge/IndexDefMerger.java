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
name|index
operator|.
name|merge
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

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
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|commons
operator|.
name|json
operator|.
name|JsonObject
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
name|index
operator|.
name|search
operator|.
name|spi
operator|.
name|query
operator|.
name|IndexName
import|;
end_import

begin_comment
comment|/**  * Utility that allows to merge index definitions.  */
end_comment

begin_class
specifier|public
class|class
name|IndexDefMerger
block|{
specifier|private
specifier|static
name|HashSet
argument_list|<
name|String
argument_list|>
name|IGNORE_LEVEL_0
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"reindex"
argument_list|,
literal|"refresh"
argument_list|,
literal|"seed"
argument_list|,
literal|"reindexCount"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Merge index definition changes.      *      * @param ancestor the common ancestor (the old product index, e.g. lucene)      * @param custom the latest customized version (e.g. lucene-custom-1)      * @param product the latest product index (e.g. lucene-2)      * @return the merged index definition (e.g. lucene-2-custom-1)      */
specifier|public
specifier|static
name|JsonObject
name|merge
parameter_list|(
name|JsonObject
name|ancestor
parameter_list|,
name|JsonObject
name|custom
parameter_list|,
name|JsonObject
name|product
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|conflicts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|JsonObject
name|merged
init|=
name|merge
argument_list|(
literal|0
argument_list|,
name|ancestor
argument_list|,
name|custom
argument_list|,
name|product
argument_list|,
name|conflicts
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|conflicts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Conflicts detected: "
operator|+
name|conflicts
argument_list|)
throw|;
block|}
return|return
name|merged
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isSame
parameter_list|(
name|String
name|a
parameter_list|,
name|String
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
operator|||
name|b
operator|==
literal|null
condition|)
block|{
return|return
name|a
operator|==
name|b
return|;
block|}
return|return
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isSame
parameter_list|(
name|JsonObject
name|a
parameter_list|,
name|JsonObject
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
operator|||
name|b
operator|==
literal|null
condition|)
block|{
return|return
name|a
operator|==
name|b
return|;
block|}
return|return
name|a
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|JsonObject
name|merge
parameter_list|(
name|int
name|level
parameter_list|,
name|JsonObject
name|ancestor
parameter_list|,
name|JsonObject
name|custom
parameter_list|,
name|JsonObject
name|product
parameter_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
name|conflicts
parameter_list|)
block|{
name|JsonObject
name|merged
init|=
operator|new
name|JsonObject
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|properties
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|ancestor
operator|==
literal|null
condition|)
block|{
name|ancestor
operator|=
operator|new
name|JsonObject
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|ancestor
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|custom
operator|==
literal|null
condition|)
block|{
name|custom
operator|=
operator|new
name|JsonObject
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|custom
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|product
operator|==
literal|null
condition|)
block|{
name|product
operator|=
operator|new
name|JsonObject
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|product
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|k
range|:
name|properties
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|level
operator|==
literal|0
operator|&&
name|IGNORE_LEVEL_0
operator|.
name|contains
argument_list|(
name|k
argument_list|)
condition|)
block|{
comment|// ignore some properties
continue|continue;
block|}
if|if
condition|(
name|k
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
comment|// ignore hidden properties
continue|continue;
block|}
name|String
name|ap
init|=
name|ancestor
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|String
name|cp
init|=
name|custom
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|String
name|pp
init|=
name|product
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|String
name|result
decl_stmt|;
if|if
condition|(
name|isSame
argument_list|(
name|ap
argument_list|,
name|pp
argument_list|)
operator|||
name|isSame
argument_list|(
name|cp
argument_list|,
name|pp
argument_list|)
condition|)
block|{
name|result
operator|=
name|cp
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isSame
argument_list|(
name|ap
argument_list|,
name|cp
argument_list|)
condition|)
block|{
name|result
operator|=
name|pp
expr_stmt|;
block|}
else|else
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"Could not merge value; property="
operator|+
name|k
operator|+
literal|"; ancestor="
operator|+
name|ap
operator|+
literal|"; custom="
operator|+
name|cp
operator|+
literal|"; product="
operator|+
name|pp
argument_list|)
expr_stmt|;
name|result
operator|=
name|ap
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|merged
operator|.
name|getProperties
argument_list|()
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|children
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|ancestor
operator|.
name|getChildren
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|children
operator|.
name|put
argument_list|(
name|c
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|c
range|:
name|custom
operator|.
name|getChildren
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|children
operator|.
name|put
argument_list|(
name|c
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|c
range|:
name|product
operator|.
name|getChildren
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|children
operator|.
name|put
argument_list|(
name|c
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|k
range|:
name|children
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|k
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
comment|// ignore hidden nodes
continue|continue;
block|}
name|JsonObject
name|a
init|=
name|ancestor
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|JsonObject
name|c
init|=
name|custom
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|JsonObject
name|p
init|=
name|product
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|JsonObject
name|result
decl_stmt|;
if|if
condition|(
name|isSame
argument_list|(
name|a
argument_list|,
name|p
argument_list|)
operator|||
name|isSame
argument_list|(
name|c
argument_list|,
name|p
argument_list|)
condition|)
block|{
name|result
operator|=
name|c
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isSame
argument_list|(
name|a
argument_list|,
name|c
argument_list|)
condition|)
block|{
name|result
operator|=
name|p
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|merge
argument_list|(
name|level
operator|+
literal|1
argument_list|,
name|a
argument_list|,
name|c
argument_list|,
name|p
argument_list|,
name|conflicts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|merged
operator|.
name|getChildren
argument_list|()
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|merged
return|;
block|}
comment|/**      * For indexes that were modified both by the customer and in the product, merge      * the changes, and create a new index.      *      * The new index (if any) is stored in the "newIndexes" object.      *      * @param newIndexes the new indexes      * @param allIndexes all index definitions (including the new ones)      */
specifier|public
specifier|static
name|void
name|merge
parameter_list|(
name|JsonObject
name|newIndexes
parameter_list|,
name|JsonObject
name|allIndexes
parameter_list|)
block|{
comment|// TODO when merging, we keep the product index, so two indexes are created.
comment|// e.g. lucene, lucene-custom-1, lucene-2, lucene-2-custom-1
comment|// but if we don't have lucene-2, then we can't merge lucene-3.
comment|// TODO when merging, e.g. lucene-2-custom-1 is created. but
comment|// it is only imported in the read-write repo, not in the read-only
comment|// repository currently. so this new index won't be used;
comment|// instead, lucene-2 will be used
name|List
argument_list|<
name|IndexName
argument_list|>
name|newNames
init|=
name|newIndexes
operator|.
name|getChildren
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|s
lambda|->
name|IndexName
operator|.
name|parse
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|newNames
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexName
argument_list|>
name|allNames
init|=
name|allIndexes
operator|.
name|getChildren
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|s
lambda|->
name|IndexName
operator|.
name|parse
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|allNames
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|JsonObject
argument_list|>
name|mergedMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexName
name|n
range|:
name|newNames
control|)
block|{
if|if
condition|(
name|n
operator|.
name|getCustomerVersion
argument_list|()
operator|==
literal|0
condition|)
block|{
name|IndexName
name|latest
init|=
name|n
operator|.
name|getLatestCustomized
argument_list|(
name|allNames
argument_list|)
decl_stmt|;
name|IndexName
name|ancestor
init|=
name|n
operator|.
name|getLatestProduct
argument_list|(
name|allNames
argument_list|)
decl_stmt|;
if|if
condition|(
name|latest
operator|!=
literal|null
operator|&&
name|ancestor
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|n
operator|.
name|compareTo
argument_list|(
name|latest
argument_list|)
operator|<=
literal|0
operator|||
name|n
operator|.
name|compareTo
argument_list|(
name|ancestor
argument_list|)
operator|<=
literal|0
condition|)
block|{
comment|// ignore older versions of indexes
continue|continue;
block|}
name|JsonObject
name|latestCustomized
init|=
name|allIndexes
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
name|latest
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|JsonObject
name|latestAncestor
init|=
name|allIndexes
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
name|ancestor
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|JsonObject
name|newProduct
init|=
name|newIndexes
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
name|n
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|JsonObject
name|merged
init|=
name|merge
argument_list|(
name|latestAncestor
argument_list|,
name|latestCustomized
argument_list|,
name|newProduct
argument_list|)
decl_stmt|;
name|mergedMap
operator|.
name|put
argument_list|(
name|n
operator|.
name|nextCustomizedName
argument_list|()
argument_list|,
name|merged
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonObject
argument_list|>
name|e
range|:
name|mergedMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|newIndexes
operator|.
name|getChildren
argument_list|()
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

