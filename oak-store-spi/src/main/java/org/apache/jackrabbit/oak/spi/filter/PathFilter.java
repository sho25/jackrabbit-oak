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
name|spi
operator|.
name|filter
package|;
end_package

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
name|Collection
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
name|Set
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
name|api
operator|.
name|Type
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
name|PathUtils
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
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
name|commons
operator|.
name|PathUtils
operator|.
name|isAncestor
import|;
end_import

begin_comment
comment|/**  * Filter which determines whether given path should be included for processing  * or not  */
end_comment

begin_class
specifier|public
class|class
name|PathFilter
block|{
specifier|private
specifier|static
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|INCLUDE_ROOT
init|=
name|singletonList
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
comment|/**      * Multi value property name used to determine list of paths to be included      */
specifier|public
specifier|static
specifier|final
name|String
name|PROP_INCLUDED_PATHS
init|=
literal|"includedPaths"
decl_stmt|;
comment|/**      * Multi value property name used to determine list of paths to be excluded      */
specifier|public
specifier|static
specifier|final
name|String
name|PROP_EXCLUDED_PATHS
init|=
literal|"excludedPaths"
decl_stmt|;
specifier|public
enum|enum
name|Result
block|{
comment|/**          * Include the path for processing          */
name|INCLUDE
block|,
comment|/**          * Exclude the path and subtree for processing          */
name|EXCLUDE
block|,
comment|/**          * Do not process the path but just perform traversal to          * child nodes. For IndexEditor it means that such nodes          * should not be indexed but editor must traverse down          */
name|TRAVERSE
block|}
specifier|private
specifier|static
specifier|final
name|PathFilter
name|ALL
init|=
operator|new
name|PathFilter
argument_list|(
name|INCLUDE_ROOT
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Result
name|filter
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
return|return
name|Result
operator|.
name|INCLUDE
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|includedPaths
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|excludedPaths
decl_stmt|;
comment|/**      * Constructs the predicate based on given definition state. It looks for      * multi value property with names {@link PathFilter#PROP_INCLUDED_PATHS}      * and {@link PathFilter#PROP_EXCLUDED_PATHS}. Both the properties are      * optional.      *       * @param defn nodestate representing the configuration. Generally it would      *            be the nodestate representing the index definition      * @return predicate based on the passed definition state      */
specifier|public
specifier|static
name|PathFilter
name|from
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|defn
parameter_list|)
block|{
if|if
condition|(
operator|!
name|defn
operator|.
name|hasProperty
argument_list|(
name|PROP_EXCLUDED_PATHS
argument_list|)
operator|&&
operator|!
name|defn
operator|.
name|hasProperty
argument_list|(
name|PROP_INCLUDED_PATHS
argument_list|)
condition|)
block|{
return|return
name|ALL
return|;
block|}
return|return
operator|new
name|PathFilter
argument_list|(
name|getStrings
argument_list|(
name|defn
argument_list|,
name|PROP_INCLUDED_PATHS
argument_list|,
name|INCLUDE_ROOT
argument_list|)
argument_list|,
name|getStrings
argument_list|(
name|defn
argument_list|,
name|PROP_EXCLUDED_PATHS
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Constructs the predicate with given included and excluded paths      *      * If both are empty then all paths would be considered to be included      *      * @param includes list of paths which should not be included      * @param excludes list of p      *                 aths which should be included      */
specifier|public
name|PathFilter
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|includes
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|includeCopy
init|=
name|newHashSet
argument_list|(
name|includes
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|excludeCopy
init|=
name|newHashSet
argument_list|(
name|excludes
argument_list|)
decl_stmt|;
name|PathUtils
operator|.
name|unifyInExcludes
argument_list|(
name|includeCopy
argument_list|,
name|excludeCopy
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
operator|!
name|includeCopy
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"No valid include provided. Includes %s, "
operator|+
literal|"Excludes %s"
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|)
expr_stmt|;
name|this
operator|.
name|includedPaths
operator|=
name|includeCopy
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|includeCopy
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|excludedPaths
operator|=
name|excludeCopy
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|excludeCopy
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**      * Determines whether given path is to be included or not      *      * @param path path to check      * @return result indicating if the path needs to be included, excluded or just traversed      */
specifier|public
name|Result
name|filter
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|excludedPath
range|:
name|excludedPaths
control|)
block|{
if|if
condition|(
name|excludedPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
name|isAncestor
argument_list|(
name|excludedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return
name|Result
operator|.
name|EXCLUDE
return|;
block|}
block|}
for|for
control|(
name|String
name|includedPath
range|:
name|includedPaths
control|)
block|{
if|if
condition|(
name|includedPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
name|isAncestor
argument_list|(
name|includedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return
name|Result
operator|.
name|INCLUDE
return|;
block|}
block|}
for|for
control|(
name|String
name|includedPath
range|:
name|includedPaths
control|)
block|{
if|if
condition|(
name|isAncestor
argument_list|(
name|path
argument_list|,
name|includedPath
argument_list|)
condition|)
block|{
return|return
name|Result
operator|.
name|TRAVERSE
return|;
block|}
block|}
return|return
name|Result
operator|.
name|EXCLUDE
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
literal|"PathFilter{"
operator|+
literal|"includedPaths="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|includedPaths
argument_list|)
operator|+
literal|", excludedPaths="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|excludedPaths
argument_list|)
operator|+
literal|'}'
return|;
block|}
specifier|private
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|defaultVal
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|STRINGS
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defaultVal
return|;
block|}
block|}
comment|/**      * Check whether this node and all descendants are included in this filter.      *       * @param path the path      * @return true if this and all descendants of this path are included in the filter      */
specifier|public
name|boolean
name|areAllDescendantsIncluded
parameter_list|(
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|excludedPath
range|:
name|excludedPaths
control|)
block|{
if|if
condition|(
name|excludedPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
name|isAncestor
argument_list|(
name|excludedPath
argument_list|,
name|path
argument_list|)
operator|||
name|isAncestor
argument_list|(
name|path
argument_list|,
name|excludedPath
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
for|for
control|(
name|String
name|includedPath
range|:
name|includedPaths
control|)
block|{
if|if
condition|(
name|includedPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
name|isAncestor
argument_list|(
name|includedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

