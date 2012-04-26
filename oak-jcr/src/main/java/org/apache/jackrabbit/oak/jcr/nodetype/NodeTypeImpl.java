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
name|jcr
operator|.
name|nodetype
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
name|Collection
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
name|LinkedList
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
name|Queue
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|OnParentVersionAction
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
name|commons
operator|.
name|iterator
operator|.
name|NodeTypeIteratorAdapter
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
name|namepath
operator|.
name|NameMapper
import|;
end_import

begin_class
class|class
name|NodeTypeImpl
implements|implements
name|NodeType
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|CND_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(> (\\S+(, \\S+)*))?(\n  mixin)?(\n  abstract)?"
operator|+
literal|"(\n  orderable)?(\n  primaryitem (\\S+))?(\n.*)*"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|DEF_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"  ([\\+\\-]) (\\S+) \\((.+?)\\)( = (\\S+))"
operator|+
literal|"(( (mandatory|autocreated|protected|multiple))*)"
operator|+
literal|"( ([A-Z])+)?.*"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeTypeManager
name|manager
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|declaredSuperTypeNames
decl_stmt|;
specifier|private
name|boolean
name|isAbstract
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|mixin
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|hasOrderableChildNodes
decl_stmt|;
specifier|private
specifier|final
name|String
name|primaryItemName
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|PropertyDefinition
argument_list|>
name|declaredPropertyDefinitions
init|=
operator|new
name|ArrayList
argument_list|<
name|PropertyDefinition
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|NodeDefinition
argument_list|>
name|declaredChildNodeDefinitions
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeDefinition
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|NodeTypeImpl
parameter_list|(
name|NodeTypeManager
name|manager
parameter_list|,
name|NameMapper
name|mapper
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|cnd
parameter_list|)
block|{
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|Matcher
name|matcher
init|=
name|CND_PATTERN
operator|.
name|matcher
argument_list|(
name|cnd
operator|.
name|replace
argument_list|(
literal|"\r\n"
argument_list|,
literal|"\n"
argument_list|)
argument_list|)
decl_stmt|;
name|matcher
operator|.
name|matches
argument_list|()
expr_stmt|;
name|this
operator|.
name|isAbstract
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|5
argument_list|)
operator|!=
literal|null
expr_stmt|;
name|this
operator|.
name|mixin
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
operator|!=
literal|null
expr_stmt|;
name|this
operator|.
name|hasOrderableChildNodes
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|7
argument_list|)
operator|!=
literal|null
expr_stmt|;
name|this
operator|.
name|primaryItemName
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|String
name|supertypes
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|supertypes
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|declaredSuperTypeNames
operator|=
name|supertypes
operator|.
name|split
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mixin
condition|)
block|{
name|this
operator|.
name|declaredSuperTypeNames
operator|=
operator|new
name|String
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|declaredSuperTypeNames
operator|=
operator|new
name|String
index|[]
block|{
literal|"nt:base"
block|}
expr_stmt|;
block|}
name|String
name|defs
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|9
argument_list|)
decl_stmt|;
if|if
condition|(
name|defs
operator|!=
literal|null
condition|)
block|{
name|defs
operator|=
name|defs
operator|.
name|trim
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|line
range|:
name|defs
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|matcher
operator|=
name|DEF_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|matcher
operator|.
name|matches
argument_list|()
expr_stmt|;
name|String
name|defName
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|defType
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|boolean
name|mandatory
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|6
argument_list|)
operator|.
name|contains
argument_list|(
literal|" mandatory"
argument_list|)
decl_stmt|;
name|boolean
name|autoCreated
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|6
argument_list|)
operator|.
name|contains
argument_list|(
literal|" autocreated"
argument_list|)
decl_stmt|;
name|boolean
name|isProtected
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|6
argument_list|)
operator|.
name|contains
argument_list|(
literal|" protected"
argument_list|)
decl_stmt|;
name|boolean
name|multiple
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|6
argument_list|)
operator|.
name|contains
argument_list|(
literal|" multiple"
argument_list|)
decl_stmt|;
name|int
name|onParentVersionAction
init|=
name|OnParentVersionAction
operator|.
name|COPY
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|group
argument_list|(
literal|9
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|onParentVersionAction
operator|=
name|OnParentVersionAction
operator|.
name|valueFromName
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"+"
operator|.
name|equals
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|declaredChildNodeDefinitions
operator|.
name|add
argument_list|(
operator|new
name|NodeDefinitionImpl
argument_list|(
name|this
argument_list|,
name|mapper
argument_list|,
name|defName
argument_list|,
name|autoCreated
argument_list|,
name|mandatory
argument_list|,
name|onParentVersionAction
argument_list|,
name|isProtected
argument_list|,
name|manager
argument_list|,
name|defType
operator|.
name|split
argument_list|(
literal|", "
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|declaredPropertyDefinitions
operator|.
name|add
argument_list|(
operator|new
name|PropertyDefinitionImpl
argument_list|(
name|this
argument_list|,
name|mapper
argument_list|,
name|defName
argument_list|,
name|autoCreated
argument_list|,
name|mandatory
argument_list|,
name|onParentVersionAction
argument_list|,
name|isProtected
argument_list|,
name|PropertyType
operator|.
name|valueFromName
argument_list|(
name|defType
argument_list|)
argument_list|,
name|multiple
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|mapper
operator|.
name|getJcrName
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getDeclaredSupertypeNames
parameter_list|()
block|{
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|declaredSuperTypeNames
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|names
index|[
name|i
index|]
operator|=
name|mapper
operator|.
name|getJcrName
argument_list|(
name|declaredSuperTypeNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAbstract
parameter_list|()
block|{
return|return
name|isAbstract
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMixin
parameter_list|()
block|{
return|return
name|mixin
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasOrderableChildNodes
parameter_list|()
block|{
return|return
name|hasOrderableChildNodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isQueryable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPrimaryItemName
parameter_list|()
block|{
if|if
condition|(
name|primaryItemName
operator|!=
literal|null
condition|)
block|{
return|return
name|mapper
operator|.
name|getJcrName
argument_list|(
name|primaryItemName
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinition
index|[]
name|getDeclaredPropertyDefinitions
parameter_list|()
block|{
return|return
name|declaredPropertyDefinitions
operator|.
name|toArray
argument_list|(
operator|new
name|PropertyDefinition
index|[
name|declaredPropertyDefinitions
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinition
index|[]
name|getDeclaredChildNodeDefinitions
parameter_list|()
block|{
return|return
name|declaredChildNodeDefinitions
operator|.
name|toArray
argument_list|(
operator|new
name|NodeDefinition
index|[
name|declaredChildNodeDefinitions
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
index|[]
name|getSupertypes
parameter_list|()
block|{
try|try
block|{
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|added
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Queue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getDeclaredSupertypeNames
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|mapper
operator|.
name|getJcrName
argument_list|(
name|queue
operator|.
name|remove
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|added
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeType
name|type
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getDeclaredSupertypeNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|types
operator|.
name|toArray
argument_list|(
operator|new
name|NodeType
index|[
name|types
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Inconsistent node type: "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeType
index|[]
name|getDeclaredSupertypes
parameter_list|()
block|{
try|try
block|{
name|String
index|[]
name|names
init|=
name|getDeclaredSupertypeNames
argument_list|()
decl_stmt|;
name|NodeType
index|[]
name|types
init|=
operator|new
name|NodeType
index|[
name|names
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|types
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Inconsistent node type: "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getSubtypes
parameter_list|()
block|{
try|try
block|{
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
name|NodeTypeIterator
name|iterator
init|=
name|manager
operator|.
name|getAllNodeTypes
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NodeType
name|type
init|=
name|iterator
operator|.
name|nextNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isNodeType
argument_list|(
name|getName
argument_list|()
argument_list|)
operator|&&
operator|!
name|isNodeType
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|types
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Inconsistent node type: "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getDeclaredSubtypes
parameter_list|()
block|{
try|try
block|{
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|(
name|declaredSuperTypeNames
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|declaredSuperTypeNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|types
operator|.
name|add
argument_list|(
name|manager
operator|.
name|getNodeType
argument_list|(
name|mapper
operator|.
name|getJcrName
argument_list|(
name|declaredSuperTypeNames
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|types
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Inconsistent node type: "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNodeType
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
block|{
if|if
condition|(
name|nodeTypeName
operator|.
name|equals
argument_list|(
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|NodeType
name|type
range|:
name|getSupertypes
argument_list|()
control|)
block|{
if|if
condition|(
name|nodeTypeName
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getName
argument_list|()
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
annotation|@
name|Override
specifier|public
name|PropertyDefinition
index|[]
name|getPropertyDefinitions
parameter_list|()
block|{
name|Collection
argument_list|<
name|PropertyDefinition
argument_list|>
name|definitions
init|=
operator|new
name|ArrayList
argument_list|<
name|PropertyDefinition
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|type
range|:
name|getSupertypes
argument_list|()
control|)
block|{
name|definitions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getDeclaredPropertyDefinitions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|definitions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getDeclaredPropertyDefinitions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|definitions
operator|.
name|toArray
argument_list|(
operator|new
name|PropertyDefinition
index|[
name|definitions
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinition
index|[]
name|getChildNodeDefinitions
parameter_list|()
block|{
name|Collection
argument_list|<
name|NodeDefinition
argument_list|>
name|definitions
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeDefinition
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|type
range|:
name|getSupertypes
argument_list|()
control|)
block|{
name|definitions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getDeclaredChildNodeDefinitions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|definitions
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getDeclaredChildNodeDefinitions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|definitions
operator|.
name|toArray
argument_list|(
operator|new
name|NodeDefinition
index|[
name|definitions
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canSetProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Value
name|value
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// TODO
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canSetProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Value
index|[]
name|values
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// TODO
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canAddChildNode
parameter_list|(
name|String
name|childNodeName
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// TODO
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canAddChildNode
parameter_list|(
name|String
name|childNodeName
parameter_list|,
name|String
name|nodeTypeName
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// TODO
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRemoveItem
parameter_list|(
name|String
name|itemName
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// TODO
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRemoveNode
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// TODO
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRemoveProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// TODO
block|}
block|}
end_class

end_unit

