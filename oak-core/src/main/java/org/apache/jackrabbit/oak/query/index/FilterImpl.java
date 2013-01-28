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
name|query
operator|.
name|index
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
name|Collection
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
name|Nullable
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
name|PropertyValue
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
name|query
operator|.
name|ast
operator|.
name|Operator
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
name|query
operator|.
name|ast
operator|.
name|SelectorImpl
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

begin_comment
comment|/**  * A filter or lookup condition.  */
end_comment

begin_class
specifier|public
class|class
name|FilterImpl
implements|implements
name|Filter
block|{
comment|/**      * The selector this filter applies to.      */
specifier|private
specifier|final
name|SelectorImpl
name|selector
decl_stmt|;
specifier|private
specifier|final
name|String
name|queryStatement
decl_stmt|;
comment|/**      * Whether the filter is always false.      */
specifier|private
name|boolean
name|alwaysFalse
decl_stmt|;
comment|/**      *  The path, or "/" (the root node, meaning no filter) if not set.      */
specifier|private
name|String
name|path
init|=
literal|"/"
decl_stmt|;
specifier|private
name|PathRestriction
name|pathRestriction
init|=
name|PathRestriction
operator|.
name|NO_RESTRICTION
decl_stmt|;
comment|/**      *  The node type, or null if not set.      */
specifier|private
name|String
name|nodeType
decl_stmt|;
comment|/**      * The fulltext search conditions, if any.      */
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fulltextConditions
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyRestriction
argument_list|>
name|propertyRestrictions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyRestriction
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Only return distinct values.      */
specifier|private
name|boolean
name|distinct
decl_stmt|;
comment|/**      * Set during the prepare phase of a query.      */
specifier|private
name|boolean
name|preparing
decl_stmt|;
comment|// TODO support "order by"
specifier|public
name|FilterImpl
parameter_list|(
name|SelectorImpl
name|selector
parameter_list|,
name|String
name|queryStatement
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|queryStatement
operator|=
name|queryStatement
expr_stmt|;
block|}
specifier|public
name|void
name|setPreparing
parameter_list|(
name|boolean
name|preparing
parameter_list|)
block|{
name|this
operator|.
name|preparing
operator|=
name|preparing
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPreparing
parameter_list|()
block|{
return|return
name|preparing
return|;
block|}
comment|/**      * Get the path.      *      * @return the path      */
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|PathRestriction
name|getPathRestriction
parameter_list|()
block|{
return|return
name|pathRestriction
return|;
block|}
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getNodeType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
specifier|public
name|void
name|setNodeType
parameter_list|(
name|String
name|nodeType
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
name|distinct
return|;
block|}
specifier|public
name|void
name|setDistinct
parameter_list|(
name|boolean
name|distinct
parameter_list|)
block|{
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
block|}
specifier|public
name|void
name|setAlwaysFalse
parameter_list|()
block|{
name|propertyRestrictions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodeType
operator|=
literal|""
expr_stmt|;
name|path
operator|=
literal|"/"
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|EXACT
expr_stmt|;
name|alwaysFalse
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAlwaysFalse
parameter_list|()
block|{
return|return
name|alwaysFalse
return|;
block|}
specifier|public
name|SelectorImpl
name|getSelector
parameter_list|()
block|{
return|return
name|selector
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PropertyRestriction
argument_list|>
name|getPropertyRestrictions
parameter_list|()
block|{
return|return
name|propertyRestrictions
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**      * Get the restriction for the given property, if any.      *      * @param propertyName the property name      * @return the restriction or null      */
annotation|@
name|Override
specifier|public
name|PropertyRestriction
name|getPropertyRestriction
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|propertyRestrictions
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|testPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|isAlwaysFalse
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
return|return
literal|true
return|;
case|case
name|EXACT
case|:
return|return
name|path
operator|.
name|matches
argument_list|(
name|this
operator|.
name|path
argument_list|)
return|;
case|case
name|PARENT
case|:
return|return
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|this
operator|.
name|path
argument_list|)
return|;
case|case
name|DIRECT_CHILDREN
case|:
return|return
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
operator|.
name|equals
argument_list|(
name|this
operator|.
name|path
argument_list|)
return|;
case|case
name|ALL_CHILDREN
case|:
return|return
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|this
operator|.
name|path
argument_list|,
name|path
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown path restriction: "
operator|+
name|pathRestriction
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|restrictPropertyType
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|int
name|propertyType
parameter_list|)
block|{
if|if
condition|(
name|propertyType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
comment|// not restricted
return|return;
block|}
name|PropertyRestriction
name|x
init|=
name|propertyRestrictions
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|==
literal|null
condition|)
block|{
name|x
operator|=
operator|new
name|PropertyRestriction
argument_list|()
expr_stmt|;
name|x
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|propertyRestrictions
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|x
operator|.
name|propertyType
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
operator|&&
name|x
operator|.
name|propertyType
operator|!=
name|propertyType
condition|)
block|{
comment|// already restricted to another property type - always false
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
name|x
operator|.
name|propertyType
operator|=
name|propertyType
expr_stmt|;
block|}
specifier|public
name|void
name|restrictProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Operator
name|op
parameter_list|,
name|PropertyValue
name|v
parameter_list|)
block|{
name|PropertyRestriction
name|x
init|=
name|propertyRestrictions
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|==
literal|null
condition|)
block|{
name|x
operator|=
operator|new
name|PropertyRestriction
argument_list|()
expr_stmt|;
name|x
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|propertyRestrictions
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
name|PropertyValue
name|oldFirst
init|=
name|x
operator|.
name|first
decl_stmt|;
name|PropertyValue
name|oldLast
init|=
name|x
operator|.
name|last
decl_stmt|;
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|EQUAL
case|:
name|x
operator|.
name|first
operator|=
name|maxValue
argument_list|(
name|oldFirst
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|x
operator|.
name|firstIncluding
operator|=
name|x
operator|.
name|first
operator|==
name|oldFirst
condition|?
name|x
operator|.
name|firstIncluding
else|:
literal|true
expr_stmt|;
name|x
operator|.
name|last
operator|=
name|minValue
argument_list|(
name|oldLast
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|x
operator|.
name|lastIncluding
operator|=
name|x
operator|.
name|last
operator|==
name|oldLast
condition|?
name|x
operator|.
name|lastIncluding
else|:
literal|true
expr_stmt|;
break|break;
case|case
name|NOT_EQUAL
case|:
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"NOT_EQUAL only supported for NOT_EQUAL NULL"
argument_list|)
throw|;
block|}
break|break;
case|case
name|GREATER_THAN
case|:
name|x
operator|.
name|first
operator|=
name|maxValue
argument_list|(
name|oldFirst
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|x
operator|.
name|firstIncluding
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|GREATER_OR_EQUAL
case|:
name|x
operator|.
name|first
operator|=
name|maxValue
argument_list|(
name|oldFirst
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|x
operator|.
name|firstIncluding
operator|=
name|x
operator|.
name|first
operator|==
name|oldFirst
condition|?
name|x
operator|.
name|firstIncluding
else|:
literal|true
expr_stmt|;
break|break;
case|case
name|LESS_THAN
case|:
name|x
operator|.
name|last
operator|=
name|minValue
argument_list|(
name|oldLast
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|x
operator|.
name|lastIncluding
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|LESS_OR_EQUAL
case|:
name|x
operator|.
name|last
operator|=
name|minValue
argument_list|(
name|oldLast
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|x
operator|.
name|lastIncluding
operator|=
name|x
operator|.
name|last
operator|==
name|oldLast
condition|?
name|x
operator|.
name|lastIncluding
else|:
literal|true
expr_stmt|;
break|break;
case|case
name|LIKE
case|:
comment|// LIKE is handled in the fulltext index
name|x
operator|.
name|isLike
operator|=
literal|true
expr_stmt|;
name|x
operator|.
name|first
operator|=
name|v
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|x
operator|.
name|first
operator|!=
literal|null
operator|&&
name|x
operator|.
name|last
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|x
operator|.
name|first
operator|.
name|compareTo
argument_list|(
name|x
operator|.
name|last
argument_list|)
operator|>
literal|0
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|x
operator|.
name|first
operator|.
name|compareTo
argument_list|(
name|x
operator|.
name|last
argument_list|)
operator|==
literal|0
operator|&&
operator|(
operator|!
name|x
operator|.
name|firstIncluding
operator|||
operator|!
name|x
operator|.
name|lastIncluding
operator|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|static
name|PropertyValue
name|maxValue
parameter_list|(
name|PropertyValue
name|a
parameter_list|,
name|PropertyValue
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
return|return
name|b
return|;
block|}
return|return
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<
literal|0
condition|?
name|b
else|:
name|a
return|;
block|}
specifier|static
name|PropertyValue
name|minValue
parameter_list|(
name|PropertyValue
name|a
parameter_list|,
name|PropertyValue
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
return|return
name|b
return|;
block|}
return|return
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<=
literal|0
condition|?
name|a
else|:
name|b
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryStatement
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"query "
argument_list|)
operator|.
name|append
argument_list|(
name|queryStatement
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|alwaysFalse
condition|)
block|{
return|return
literal|"(always false)"
return|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"path: "
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
name|pathRestriction
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|PropertyRestriction
argument_list|>
name|p
range|:
name|propertyRestrictions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"property "
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|restrictPath
parameter_list|(
name|String
name|addedPath
parameter_list|,
name|PathRestriction
name|addedPathRestriction
parameter_list|)
block|{
if|if
condition|(
name|addedPath
operator|==
literal|null
condition|)
block|{
comment|// currently unknown (prepare time)
name|addedPath
operator|=
literal|"/"
expr_stmt|;
block|}
comment|// calculating the intersection of path restrictions
comment|// this is ugly code, but I don't currently see a radically simpler method
switch|switch
condition|(
name|addedPathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
break|break;
case|case
name|PARENT
case|:
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
break|break;
case|case
name|PARENT
case|:
comment|// ignore as it's fast anyway
comment|// (would need to loop to find a common ancestor)
break|break;
case|case
name|EXACT
case|:
case|case
name|ALL_CHILDREN
case|:
case|case
name|DIRECT_CHILDREN
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|PARENT
expr_stmt|;
name|path
operator|=
name|addedPath
expr_stmt|;
break|break;
case|case
name|EXACT
case|:
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
break|break;
case|case
name|PARENT
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|EXACT
case|:
if|if
condition|(
operator|!
name|addedPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|ALL_CHILDREN
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|addedPath
argument_list|)
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
name|path
operator|=
name|addedPath
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|EXACT
expr_stmt|;
break|break;
case|case
name|ALL_CHILDREN
case|:
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
name|path
operator|=
name|addedPath
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|ALL_CHILDREN
expr_stmt|;
break|break;
case|case
name|PARENT
case|:
case|case
name|EXACT
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|ALL_CHILDREN
case|:
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|addedPath
argument_list|)
condition|)
block|{
name|path
operator|=
name|addedPath
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
operator|&&
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
operator|&&
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
name|path
operator|=
name|addedPath
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
expr_stmt|;
break|break;
case|case
name|PARENT
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|EXACT
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|ALL_CHILDREN
case|:
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
operator|&&
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|addedPath
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
expr_stmt|;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
break|break;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFulltextConditions
parameter_list|()
block|{
comment|// TODO support fulltext conditions on certain properties
return|return
name|fulltextConditions
return|;
block|}
specifier|public
name|void
name|restrictFulltextCondition
parameter_list|(
name|String
name|condition
parameter_list|)
block|{
name|fulltextConditions
operator|.
name|add
argument_list|(
name|condition
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|getQueryStatement
parameter_list|()
block|{
return|return
name|queryStatement
return|;
block|}
block|}
end_class

end_unit

