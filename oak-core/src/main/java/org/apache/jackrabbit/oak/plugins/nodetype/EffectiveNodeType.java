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
name|nodetype
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|UNDEFINED
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
name|NT_BASE
import|;
end_import

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
name|UnsupportedRepositoryOperationException
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
name|PropertyDefinition
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
name|plugins
operator|.
name|value
operator|.
name|ValueFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Predicate
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
name|Maps
import|;
end_import

begin_comment
comment|/**  * EffectiveNodeType... TODO  */
end_comment

begin_class
specifier|public
class|class
name|EffectiveNodeType
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EffectiveNodeType
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|NodeTypeImpl
index|[]
name|NO_MIXINS
init|=
operator|new
name|NodeTypeImpl
index|[
literal|0
index|]
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeTypeImpl
argument_list|>
name|nodeTypes
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
name|EffectiveNodeType
parameter_list|(
name|NodeTypeImpl
name|primary
parameter_list|,
name|NodeTypeImpl
index|[]
name|mixins
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|)
block|{
name|this
operator|.
name|ntMgr
operator|=
name|ntMgr
expr_stmt|;
name|addNodeType
argument_list|(
name|checkNotNull
argument_list|(
name|primary
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeTypeImpl
name|mixin
range|:
name|checkNotNull
argument_list|(
name|mixins
argument_list|)
control|)
block|{
name|addNodeType
argument_list|(
name|mixin
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|nodeTypes
operator|.
name|containsKey
argument_list|(
name|NT_BASE
argument_list|)
condition|)
block|{
try|try
block|{
name|nodeTypes
operator|.
name|put
argument_list|(
name|NT_BASE
argument_list|,
operator|(
name|NodeTypeImpl
operator|)
name|ntMgr
operator|.
name|getNodeType
argument_list|(
name|NT_BASE
argument_list|)
argument_list|)
expr_stmt|;
comment|// FIXME
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// TODO: ignore/warning/error?
block|}
block|}
block|}
name|EffectiveNodeType
parameter_list|(
name|NodeTypeImpl
name|primary
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|)
block|{
name|this
argument_list|(
name|primary
argument_list|,
name|NO_MIXINS
argument_list|,
name|ntMgr
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addNodeType
parameter_list|(
name|NodeTypeImpl
name|type
parameter_list|)
block|{
name|String
name|name
init|=
name|type
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|nodeTypes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|nodeTypes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|NodeType
index|[]
name|supertypes
init|=
name|type
operator|.
name|getDeclaredSupertypes
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|supertype
range|:
name|supertypes
control|)
block|{
name|addNodeType
argument_list|(
operator|(
name|NodeTypeImpl
operator|)
name|supertype
argument_list|)
expr_stmt|;
comment|// FIXME
block|}
block|}
block|}
comment|/**      * Determines whether this effective node type representation includes      * (either through inheritance or aggregation) the given node type.      *      * @param nodeTypeName name of node type      * @return {@code true} if the given node type is included, otherwise {@code false}.      */
specifier|public
name|boolean
name|includesNodeType
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
block|{
return|return
name|nodeTypes
operator|.
name|containsKey
argument_list|(
name|nodeTypeName
argument_list|)
return|;
block|}
comment|/**      * Determines whether this effective node type representation includes      * (either through inheritance or aggregation) all of the given node types.      *      * @param nodeTypeNames array of node type names      * @return {@code true} if all of the given node types are included,      *         otherwise {@code false}      */
specifier|public
name|boolean
name|includesNodeTypes
parameter_list|(
name|String
index|[]
name|nodeTypeNames
parameter_list|)
block|{
for|for
control|(
name|String
name|ntName
range|:
name|nodeTypeNames
control|)
block|{
if|if
condition|(
operator|!
name|includesNodeType
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Determines whether this effective node type supports adding      * the specified mixin.      * @param mixin name of mixin type      * @return {@code true} if the mixin type is supported, otherwise {@code false}      */
specifier|public
name|boolean
name|supportsMixin
parameter_list|(
name|String
name|mixin
parameter_list|)
block|{
if|if
condition|(
name|includesNodeType
argument_list|(
name|mixin
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|NodeType
name|mixinType
init|=
literal|null
decl_stmt|;
try|try
block|{
name|mixinType
operator|=
name|ntMgr
operator|.
name|internalGetNodeType
argument_list|(
name|mixin
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|mixinType
operator|.
name|isMixin
argument_list|()
operator|||
name|mixinType
operator|.
name|isAbstract
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchNodeTypeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unknown mixin type "
operator|+
name|mixin
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getNodeDefinitions
parameter_list|()
block|{
name|List
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
name|nt
range|:
name|nodeTypes
operator|.
name|values
argument_list|()
control|)
block|{
name|definitions
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|NodeTypeImpl
operator|)
name|nt
operator|)
operator|.
name|internalGetChildDefinitions
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|definitions
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getPropertyDefinitions
parameter_list|()
block|{
name|List
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
name|nt
range|:
name|nodeTypes
operator|.
name|values
argument_list|()
control|)
block|{
name|definitions
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|NodeTypeImpl
operator|)
name|nt
operator|)
operator|.
name|internalGetPropertyDefinitions
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|definitions
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getAutoCreateNodeDefinitions
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|getNodeDefinitions
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|NodeDefinition
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|NodeDefinition
name|nodeDefinition
parameter_list|)
block|{
return|return
name|nodeDefinition
operator|.
name|isAutoCreated
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getAutoCreatePropertyDefinitions
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|getPropertyDefinitions
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyDefinition
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|PropertyDefinition
name|propertyDefinition
parameter_list|)
block|{
return|return
name|propertyDefinition
operator|.
name|isAutoCreated
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getMandatoryNodeDefinitions
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|getNodeDefinitions
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|NodeDefinition
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|NodeDefinition
name|nodeDefinition
parameter_list|)
block|{
return|return
name|nodeDefinition
operator|.
name|isMandatory
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getMandatoryPropertyDefinitions
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|getPropertyDefinitions
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyDefinition
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|PropertyDefinition
name|propertyDefinition
parameter_list|)
block|{
return|return
name|propertyDefinition
operator|.
name|isMandatory
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Return all node definitions that match the specified oak name.      *      * @param oakName An internal oak name.      * @return All node definitions that match the given internal oak name.      */
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getNamedNodeDefinitions
parameter_list|(
specifier|final
name|String
name|oakName
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|nodeTypes
operator|.
name|values
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeTypeImpl
argument_list|,
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|apply
parameter_list|(
name|NodeTypeImpl
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getDeclaredNamedNodeDefinitions
argument_list|(
name|oakName
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Return all property definitions that match the specified oak name.      *      * @param oakName An internal oak name.      * @return All property definitions that match the given internal oak name.      */
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getNamedPropertyDefinitions
parameter_list|(
specifier|final
name|String
name|oakName
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|nodeTypes
operator|.
name|values
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeTypeImpl
argument_list|,
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|apply
parameter_list|(
name|NodeTypeImpl
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getDeclaredNamedPropertyDefinitions
argument_list|(
name|oakName
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Return all residual node definitions.      *      * @return All residual node definitions.      */
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getResidualNodeDefinitions
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|nodeTypes
operator|.
name|values
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeTypeImpl
argument_list|,
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|apply
parameter_list|(
name|NodeTypeImpl
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getDeclaredResidualNodeDefinitions
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Return all residual property definitions.      *      * @return All residual property definitions.      */
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getResidualPropertyDefinitions
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|nodeTypes
operator|.
name|values
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeTypeImpl
argument_list|,
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|apply
parameter_list|(
name|NodeTypeImpl
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getDeclaredResidualPropertyDefinitions
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|checkSetProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PropertyDefinition
name|definition
init|=
name|getDefinition
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|isProtected
argument_list|()
condition|)
block|{
return|return;
block|}
name|NodeType
name|nt
init|=
name|definition
operator|.
name|getDeclaringNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|values
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|property
argument_list|,
name|ntMgr
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|nt
operator|.
name|canSetProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Cannot set property '"
operator|+
name|property
operator|.
name|getName
argument_list|()
operator|+
literal|"' to '"
operator|+
name|values
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|Value
name|v
init|=
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|property
argument_list|,
name|ntMgr
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|nt
operator|.
name|canSetProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|v
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Cannot set property '"
operator|+
name|property
operator|.
name|getName
argument_list|()
operator|+
literal|"' to '"
operator|+
name|v
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|checkRemoveProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PropertyDefinition
name|definition
init|=
name|getDefinition
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|isProtected
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|definition
operator|.
name|getDeclaringNodeType
argument_list|()
operator|.
name|canRemoveProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Cannot remove property '"
operator|+
name|property
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|checkMandatoryItems
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
for|for
control|(
name|NodeType
name|nodeType
range|:
name|nodeTypes
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|PropertyDefinition
name|pd
range|:
name|nodeType
operator|.
name|getPropertyDefinitions
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|pd
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|pd
operator|.
name|isMandatory
argument_list|()
operator|&&
operator|!
name|pd
operator|.
name|isProtected
argument_list|()
operator|&&
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Property '"
operator|+
name|name
operator|+
literal|"' in '"
operator|+
name|nodeType
operator|.
name|getName
argument_list|()
operator|+
literal|"' is mandatory"
argument_list|)
throw|;
block|}
block|}
for|for
control|(
name|NodeDefinition
name|nd
range|:
name|nodeType
operator|.
name|getChildNodeDefinitions
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|nd
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|nd
operator|.
name|isMandatory
argument_list|()
operator|&&
operator|!
name|nd
operator|.
name|isProtected
argument_list|()
operator|&&
operator|!
name|tree
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Node '"
operator|+
name|name
operator|+
literal|"' in '"
operator|+
name|nodeType
operator|.
name|getName
argument_list|()
operator|+
literal|"' is mandatory"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|checkOrderableChildNodes
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
block|{
for|for
control|(
name|NodeType
name|nt
range|:
name|nodeTypes
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|nt
operator|.
name|hasOrderableChildNodes
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Child node ordering is not supported on this node"
argument_list|)
throw|;
block|}
comment|/**      * Calculates the applicable definition for the property with the specified      * characteristics under a parent with this effective type.      *      * @param propertyName The internal oak name of the property for which the      * definition should be retrieved.      * @param isMultiple {@code true} if the target property is multi-valued.      * @param type The target type of the property.      * @param exactTypeMatch {@code true} if the required type of the definition      * must exactly match the type of the target property.      * @return the applicable definition for the target property.      * @throws ConstraintViolationException If no matching definition can be found.      * @throws RepositoryException If another error occurs.      */
specifier|public
name|PropertyDefinition
name|getPropertyDefinition
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|boolean
name|isMultiple
parameter_list|,
name|int
name|type
parameter_list|,
name|boolean
name|exactTypeMatch
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
comment|// TODO: This may need to be optimized
for|for
control|(
name|PropertyDefinition
name|def
range|:
name|getNamedPropertyDefinitions
argument_list|(
name|propertyName
argument_list|)
control|)
block|{
name|int
name|defType
init|=
name|def
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
name|isMultiple
operator|==
name|def
operator|.
name|isMultiple
argument_list|()
operator|&&
operator|(
operator|!
name|exactTypeMatch
operator|||
operator|(
name|type
operator|==
name|defType
operator|||
name|UNDEFINED
operator|==
name|type
operator|||
name|UNDEFINED
operator|==
name|defType
operator|)
operator|)
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
comment|// try if there is a residual definition
for|for
control|(
name|PropertyDefinition
name|def
range|:
name|getResidualPropertyDefinitions
argument_list|()
control|)
block|{
name|int
name|defType
init|=
name|def
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
name|isMultiple
operator|==
name|def
operator|.
name|isMultiple
argument_list|()
operator|&&
operator|(
operator|!
name|exactTypeMatch
operator|||
operator|(
name|type
operator|==
name|defType
operator|||
name|UNDEFINED
operator|==
name|type
operator|||
name|UNDEFINED
operator|==
name|defType
operator|)
operator|)
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No matching property definition found for "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
comment|/**      *      * @param childName The internal oak name of the target node.      * @param childEffective      * @return      * @throws ConstraintViolationException      */
specifier|public
name|NodeDefinition
name|getNodeDefinition
parameter_list|(
name|String
name|childName
parameter_list|,
name|EffectiveNodeType
name|childEffective
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
for|for
control|(
name|NodeDefinition
name|def
range|:
name|getNamedNodeDefinitions
argument_list|(
name|childName
argument_list|)
control|)
block|{
name|boolean
name|match
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|childEffective
operator|!=
literal|null
operator|&&
operator|!
name|childEffective
operator|.
name|includesNodeTypes
argument_list|(
name|def
operator|.
name|getRequiredPrimaryTypeNames
argument_list|()
argument_list|)
condition|)
block|{
name|match
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|match
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
for|for
control|(
name|NodeDefinition
name|def
range|:
name|getResidualNodeDefinitions
argument_list|()
control|)
block|{
name|boolean
name|match
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|childEffective
operator|!=
literal|null
operator|&&
operator|!
name|childEffective
operator|.
name|includesNodeTypes
argument_list|(
name|def
operator|.
name|getRequiredPrimaryTypeNames
argument_list|()
argument_list|)
condition|)
block|{
name|match
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|match
condition|)
block|{
return|return
name|def
return|;
block|}
block|}
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No matching node definition found for "
operator|+
name|childName
argument_list|)
throw|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|PropertyDefinition
name|getDefinition
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|propertyName
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|propertyType
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
name|boolean
name|isMultiple
init|=
name|property
operator|.
name|isArray
argument_list|()
decl_stmt|;
return|return
name|getPropertyDefinition
argument_list|(
name|propertyName
argument_list|,
name|isMultiple
argument_list|,
name|propertyType
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

