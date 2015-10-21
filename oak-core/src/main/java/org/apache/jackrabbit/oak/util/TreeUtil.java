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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|Set
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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|NoSuchNodeTypeException
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
name|Iterables
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
name|JcrConstants
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
name|Tree
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
name|plugins
operator|.
name|identifier
operator|.
name|IdentifierManager
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|tree
operator|.
name|impl
operator|.
name|ImmutableTree
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
name|util
operator|.
name|ISO8601
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
name|Iterables
operator|.
name|contains
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
name|Lists
operator|.
name|newArrayList
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
name|emptyList
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
name|JcrConstants
operator|.
name|JCR_AUTOCREATED
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
name|JcrConstants
operator|.
name|JCR_CREATED
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
name|JcrConstants
operator|.
name|JCR_DEFAULTPRIMARYTYPE
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
name|JcrConstants
operator|.
name|JCR_DEFAULTVALUES
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
name|JcrConstants
operator|.
name|JCR_HASORDERABLECHILDNODES
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
name|JcrConstants
operator|.
name|JCR_ISMIXIN
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
name|JcrConstants
operator|.
name|JCR_LASTMODIFIED
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
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
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
name|JcrConstants
operator|.
name|JCR_MULTIPLE
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|JcrConstants
operator|.
name|JCR_SAMENAMESIBLINGS
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
name|JcrConstants
operator|.
name|JCR_UUID
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
name|api
operator|.
name|Type
operator|.
name|BOOLEAN
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
name|api
operator|.
name|Type
operator|.
name|DATE
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
name|api
operator|.
name|Type
operator|.
name|NAME
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
name|api
operator|.
name|Type
operator|.
name|NAMES
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
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
name|dropIndexFromName
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_CREATEDBY
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_IS_ABSTRACT
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_LASTMODIFIEDBY
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|REP_NAMED_CHILD_NODE_DEFINITIONS
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|REP_NAMED_PROPERTY_DEFINITIONS
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|REP_RESIDUAL_CHILD_NODE_DEFINITIONS
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|REP_SUPERTYPES
import|;
end_import

begin_comment
comment|/**  * Utility providing common operations for the {@code Tree} that are not provided  * by the API.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TreeUtil
block|{
specifier|private
name|TreeUtil
parameter_list|()
block|{     }
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|String
name|getPrimaryTypeName
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|getStringInternal
argument_list|(
name|tree
argument_list|,
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|propertyName
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
return|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|String
name|getString
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|getStringInternal
argument_list|(
name|tree
argument_list|,
name|propertyName
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|String
name|getStringInternal
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|propertyName
parameter_list|,
annotation|@
name|Nonnull
name|Type
argument_list|<
name|String
argument_list|>
name|type
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
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
operator|!
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|type
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
comment|/**      * Returns the boolean representation of the property with the specified      * {@code propertyName}. If the property does not exist or      * {@link org.apache.jackrabbit.oak.api.PropertyState#isArray() is an array}      * this method returns {@code false}.      *      * @param tree         The target tree.      * @param propertyName The name of the property.      * @return the boolean representation of the property state with the given      *         name. This utility returns {@code false} if the property does not exist      *         or is an multivalued property.      */
specifier|public
specifier|static
name|boolean
name|getBoolean
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|propertyName
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
return|return
name|property
operator|!=
literal|null
operator|&&
operator|!
name|property
operator|.
name|isArray
argument_list|()
operator|&&
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEAN
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|String
name|getName
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
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
name|NAME
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|NAME
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
name|Nonnull
specifier|public
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
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
name|NAMES
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|emptyList
argument_list|()
return|;
block|}
block|}
comment|/**      * Return the possibly non existing tree located at the passed {@code path} from      * the location of the start {@code tree} or {@code null} if {@code path} results      * in a parent of the root.      *      * @param tree  start tree      * @param path  path from the start tree      * @return  tree located at {@code path} from {@code start} or {@code null}      */
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|Tree
name|getTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|denotesParent
argument_list|(
name|element
argument_list|)
condition|)
block|{
if|if
condition|(
name|tree
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|tree
operator|=
name|tree
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesCurrent
argument_list|(
name|element
argument_list|)
condition|)
block|{
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
comment|// else . -> skip to next element
block|}
return|return
name|tree
return|;
block|}
specifier|public
specifier|static
name|Tree
name|addChild
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|CheckForNull
name|String
name|typeName
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|typeRoot
parameter_list|,
annotation|@
name|CheckForNull
name|String
name|userID
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|typeName
operator|==
literal|null
condition|)
block|{
name|typeName
operator|=
name|getDefaultChildType
argument_list|(
name|typeRoot
argument_list|,
name|parent
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|typeName
operator|==
literal|null
condition|)
block|{
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No default node type available for "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
name|Tree
name|type
init|=
name|typeRoot
operator|.
name|getChild
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Node type "
operator|+
name|typeName
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|getBoolean
argument_list|(
name|type
argument_list|,
name|JCR_IS_ABSTRACT
argument_list|)
comment|// OAK-1013: backwards compatibility for abstract default types
operator|&&
operator|!
name|typeName
operator|.
name|equals
argument_list|(
name|getDefaultChildType
argument_list|(
name|typeRoot
argument_list|,
name|parent
argument_list|,
name|name
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Node type "
operator|+
name|typeName
operator|+
literal|" is abstract"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|getBoolean
argument_list|(
name|type
argument_list|,
name|JCR_ISMIXIN
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Node type "
operator|+
name|typeName
operator|+
literal|" is a mixin type"
argument_list|)
throw|;
block|}
name|Tree
name|child
init|=
name|parent
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|typeName
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|getBoolean
argument_list|(
name|type
argument_list|,
name|JCR_HASORDERABLECHILDNODES
argument_list|)
condition|)
block|{
name|child
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|autoCreateItems
argument_list|(
name|child
argument_list|,
name|type
argument_list|,
name|typeRoot
argument_list|,
name|userID
argument_list|)
expr_stmt|;
return|return
name|child
return|;
block|}
specifier|public
specifier|static
name|void
name|addMixin
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|mixinName
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|typeRoot
parameter_list|,
annotation|@
name|CheckForNull
name|String
name|userID
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|type
init|=
name|typeRoot
operator|.
name|getChild
argument_list|(
name|mixinName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Node type "
operator|+
name|mixinName
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|getBoolean
argument_list|(
name|type
argument_list|,
name|JCR_IS_ABSTRACT
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Node type "
operator|+
name|mixinName
operator|+
literal|" is abstract"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|getBoolean
argument_list|(
name|type
argument_list|,
name|JCR_ISMIXIN
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Node type "
operator|+
name|mixinName
operator|+
literal|" is a not a mixin type"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|mixins
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|String
name|primary
init|=
name|getName
argument_list|(
name|tree
argument_list|,
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|primary
operator|!=
literal|null
operator|&&
name|Iterables
operator|.
name|contains
argument_list|(
name|getNames
argument_list|(
name|type
argument_list|,
name|NodeTypeConstants
operator|.
name|REP_PRIMARY_SUBTYPES
argument_list|)
argument_list|,
name|primary
argument_list|)
condition|)
block|{
return|return;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|subMixins
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|getNames
argument_list|(
name|type
argument_list|,
name|NodeTypeConstants
operator|.
name|REP_MIXIN_SUBTYPES
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|mixin
range|:
name|getNames
argument_list|(
name|tree
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
control|)
block|{
if|if
condition|(
name|mixinName
operator|.
name|equals
argument_list|(
name|mixin
argument_list|)
operator|||
name|subMixins
operator|.
name|contains
argument_list|(
name|mixin
argument_list|)
condition|)
block|{
return|return;
block|}
name|mixins
operator|.
name|add
argument_list|(
name|mixin
argument_list|)
expr_stmt|;
block|}
name|mixins
operator|.
name|add
argument_list|(
name|mixinName
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|mixins
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
name|autoCreateItems
argument_list|(
name|tree
argument_list|,
name|type
argument_list|,
name|typeRoot
argument_list|,
name|userID
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|autoCreateItems
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|type
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|typeRoot
parameter_list|,
annotation|@
name|CheckForNull
name|String
name|userID
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO: use a separate rep:autoCreatePropertyDefinitions
name|Tree
name|properties
init|=
name|type
operator|.
name|getChild
argument_list|(
name|REP_NAMED_PROPERTY_DEFINITIONS
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|definitions
range|:
name|properties
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|definitions
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|NodeTypeConstants
operator|.
name|REP_PRIMARY_TYPE
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
name|NodeTypeConstants
operator|.
name|REP_MIXIN_TYPES
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|NodeTypeConstants
operator|.
name|REP_UUID
argument_list|)
condition|)
block|{
name|name
operator|=
name|JCR_UUID
expr_stmt|;
block|}
for|for
control|(
name|Tree
name|definition
range|:
name|definitions
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|getBoolean
argument_list|(
name|definition
argument_list|,
name|JCR_AUTOCREATED
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|tree
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|PropertyState
name|property
init|=
name|autoCreateProperty
argument_list|(
name|name
argument_list|,
name|definition
argument_list|,
name|userID
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Unable to auto-create value for "
operator|+
name|PathUtils
operator|.
name|concat
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|)
throw|;
block|}
block|}
break|break;
block|}
block|}
block|}
comment|// TODO: use a separate rep:autoCreateChildNodeDefinitions
comment|// Note that we use only named, non-SNS child node definitions
comment|// as there can be no reasonable default values for residual or
comment|// SNS child nodes
name|Tree
name|childNodes
init|=
name|type
operator|.
name|getChild
argument_list|(
name|REP_NAMED_CHILD_NODE_DEFINITIONS
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|definitions
range|:
name|childNodes
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|definitions
operator|.
name|getName
argument_list|()
decl_stmt|;
for|for
control|(
name|Tree
name|definition
range|:
name|definitions
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|getBoolean
argument_list|(
name|definition
argument_list|,
name|JCR_AUTOCREATED
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|tree
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|typeName
init|=
name|getName
argument_list|(
name|definition
argument_list|,
name|JCR_DEFAULTPRIMARYTYPE
argument_list|)
decl_stmt|;
name|addChild
argument_list|(
name|tree
argument_list|,
name|name
argument_list|,
name|typeName
argument_list|,
name|typeRoot
argument_list|,
name|userID
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|PropertyState
name|autoCreateProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|definition
parameter_list|,
annotation|@
name|CheckForNull
name|String
name|userID
parameter_list|)
block|{
if|if
condition|(
name|JCR_UUID
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|uuid
init|=
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
decl_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|uuid
argument_list|,
name|STRING
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|JCR_CREATED
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|now
init|=
name|ISO8601
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|now
argument_list|,
name|DATE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|JCR_CREATEDBY
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|userID
operator|!=
literal|null
condition|)
block|{
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|userID
argument_list|,
name|STRING
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|JCR_LASTMODIFIED
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|now
init|=
name|ISO8601
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|now
argument_list|,
name|DATE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|JCR_LASTMODIFIEDBY
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|userID
operator|!=
literal|null
condition|)
block|{
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|userID
argument_list|,
name|STRING
argument_list|)
return|;
block|}
block|}
comment|// does the definition have a default value?
name|PropertyState
name|values
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|JCR_DEFAULTVALUES
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|values
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|getBoolean
argument_list|(
name|definition
argument_list|,
name|JCR_MULTIPLE
argument_list|)
condition|)
block|{
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|values
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|values
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
name|type
operator|=
name|type
operator|.
name|getBaseType
argument_list|()
expr_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|values
operator|.
name|getValue
argument_list|(
name|type
argument_list|,
literal|0
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Finds the default primary type for a new child node with the given name.      *      * @param typeRoot root of the {@code /jcr:system/jcr:nodeTypes} tree      * @param parent parent node      * @param childName name of the new child node      * @return name of the default type, or {@code null} if not available      */
specifier|public
specifier|static
name|String
name|getDefaultChildType
parameter_list|(
name|Tree
name|typeRoot
parameter_list|,
name|Tree
name|parent
parameter_list|,
name|String
name|childName
parameter_list|)
block|{
name|String
name|name
init|=
name|dropIndexFromName
argument_list|(
name|childName
argument_list|)
decl_stmt|;
name|boolean
name|sns
init|=
operator|!
name|name
operator|.
name|equals
argument_list|(
name|childName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Tree
argument_list|>
name|types
init|=
name|getEffectiveType
argument_list|(
name|parent
argument_list|,
name|typeRoot
argument_list|)
decl_stmt|;
comment|// first look for named node definitions
for|for
control|(
name|Tree
name|type
range|:
name|types
control|)
block|{
name|Tree
name|definitions
init|=
name|type
operator|.
name|getChild
argument_list|(
name|REP_NAMED_CHILD_NODE_DEFINITIONS
argument_list|)
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|defaultName
init|=
name|findDefaultPrimaryType
argument_list|(
name|definitions
argument_list|,
name|sns
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultName
operator|!=
literal|null
condition|)
block|{
return|return
name|defaultName
return|;
block|}
block|}
comment|// then check residual definitions
for|for
control|(
name|Tree
name|type
range|:
name|types
control|)
block|{
name|Tree
name|definitions
init|=
name|type
operator|.
name|getChild
argument_list|(
name|REP_RESIDUAL_CHILD_NODE_DEFINITIONS
argument_list|)
decl_stmt|;
name|String
name|defaultName
init|=
name|findDefaultPrimaryType
argument_list|(
name|definitions
argument_list|,
name|sns
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultName
operator|!=
literal|null
condition|)
block|{
return|return
name|defaultName
return|;
block|}
block|}
comment|// no matching child node definition found
return|return
literal|null
return|;
block|}
comment|/**      * Returns the effective node types of the given node.      */
specifier|public
specifier|static
name|List
argument_list|<
name|Tree
argument_list|>
name|getEffectiveType
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|Tree
name|typeRoot
parameter_list|)
block|{
name|List
argument_list|<
name|Tree
argument_list|>
name|types
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|String
name|primary
init|=
name|getName
argument_list|(
name|tree
argument_list|,
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|primary
operator|!=
literal|null
condition|)
block|{
name|Tree
name|type
init|=
name|typeRoot
operator|.
name|getChild
argument_list|(
name|primary
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|exists
argument_list|()
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
for|for
control|(
name|String
name|mixin
range|:
name|getNames
argument_list|(
name|tree
argument_list|,
name|JCR_MIXINTYPES
argument_list|)
control|)
block|{
name|Tree
name|type
init|=
name|typeRoot
operator|.
name|getChild
argument_list|(
name|mixin
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|exists
argument_list|()
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
name|types
return|;
block|}
specifier|public
specifier|static
name|String
name|findDefaultPrimaryType
parameter_list|(
name|Tree
name|definitions
parameter_list|,
name|boolean
name|sns
parameter_list|)
block|{
for|for
control|(
name|Tree
name|definition
range|:
name|definitions
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|String
name|defaultName
init|=
name|getName
argument_list|(
name|definition
argument_list|,
name|JCR_DEFAULTPRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultName
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|sns
operator|||
name|getBoolean
argument_list|(
name|definition
argument_list|,
name|JCR_SAMENAMESIBLINGS
argument_list|)
operator|)
condition|)
block|{
return|return
name|defaultName
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isNodeType
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|typeName
parameter_list|,
name|Tree
name|typeRoot
parameter_list|)
block|{
name|String
name|primaryName
init|=
name|TreeUtil
operator|.
name|getName
argument_list|(
name|tree
argument_list|,
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeName
operator|.
name|equals
argument_list|(
name|primaryName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|primaryName
operator|!=
literal|null
condition|)
block|{
name|Tree
name|type
init|=
name|typeRoot
operator|.
name|getChild
argument_list|(
name|primaryName
argument_list|)
decl_stmt|;
if|if
condition|(
name|contains
argument_list|(
name|getNames
argument_list|(
name|type
argument_list|,
name|REP_SUPERTYPES
argument_list|)
argument_list|,
name|typeName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
for|for
control|(
name|String
name|mixinName
range|:
name|getNames
argument_list|(
name|tree
argument_list|,
name|JCR_MIXINTYPES
argument_list|)
control|)
block|{
if|if
condition|(
name|typeName
operator|.
name|equals
argument_list|(
name|mixinName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|Tree
name|type
init|=
name|typeRoot
operator|.
name|getChild
argument_list|(
name|mixinName
argument_list|)
decl_stmt|;
if|if
condition|(
name|contains
argument_list|(
name|getNames
argument_list|(
name|type
argument_list|,
name|REP_SUPERTYPES
argument_list|)
argument_list|,
name|typeName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns {@code true} if the specified {@code tree} is a read-only tree      * such as obtained through {@link org.apache.jackrabbit.oak.plugins.tree.TreeFactory}      * or a {@link org.apache.jackrabbit.oak.plugins.tree.RootFactory read-only Root}.      *      * @param tree The tree object to be tested.      * @return {@code true} if the specified tree is an immutable read-only tree.      * @see org.apache.jackrabbit.oak.plugins.tree.TreeFactory#createReadOnlyTree(org.apache.jackrabbit.oak.spi.state.NodeState)      * @see org.apache.jackrabbit.oak.plugins.tree.RootFactory#createReadOnlyRoot(org.apache.jackrabbit.oak.spi.state.NodeState)      */
specifier|public
specifier|static
name|boolean
name|isReadOnlyTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|tree
operator|instanceof
name|ImmutableTree
return|;
block|}
block|}
end_class

end_unit

