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
name|HashSet
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
name|Nullable
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
name|ItemDefinition
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

begin_comment
comment|/**  * EffectiveNodeTypeImpl...  *  * TODO implementation needs optimization  */
end_comment

begin_class
class|class
name|EffectiveNodeTypeImpl
implements|implements
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
name|EffectiveNodeTypeImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|nodeTypes
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
name|EffectiveNodeTypeImpl
parameter_list|(
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|nodeTypes
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|)
block|{
name|this
operator|.
name|nodeTypes
operator|=
name|nodeTypes
expr_stmt|;
name|this
operator|.
name|ntMgr
operator|=
name|ntMgr
expr_stmt|;
block|}
specifier|static
name|EffectiveNodeType
name|create
parameter_list|(
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|nodeTypes
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
if|if
condition|(
operator|!
name|isValid
argument_list|(
name|nodeTypes
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Invalid effective node type"
argument_list|)
throw|;
block|}
return|return
operator|new
name|EffectiveNodeTypeImpl
argument_list|(
name|nodeTypes
argument_list|,
name|ntMgr
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isValid
parameter_list|(
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|nodeTypes
parameter_list|)
block|{
comment|// FIXME: add validation
return|return
literal|true
return|;
block|}
comment|//---------------------------------------------=----< EffectiveNodeType>---
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|NodeType
argument_list|>
name|getAllNodeTypes
parameter_list|()
block|{
return|return
name|nodeTypes
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includesNodeType
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
block|{
for|for
control|(
name|NodeType
name|type
range|:
name|nodeTypes
control|)
block|{
if|if
condition|(
name|type
operator|.
name|isNodeType
argument_list|(
name|nodeTypeName
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
annotation|@
name|Override
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
if|if
condition|(
name|mixinType
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|NodeType
argument_list|>
name|newTypes
init|=
operator|new
name|HashSet
argument_list|<
name|NodeType
argument_list|>
argument_list|(
name|nodeTypes
argument_list|)
decl_stmt|;
name|newTypes
operator|.
name|add
argument_list|(
name|mixinType
argument_list|)
expr_stmt|;
return|return
name|isValid
argument_list|(
name|newTypes
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getNamedNodeDefinitions
parameter_list|(
name|String
name|oakName
parameter_list|)
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
name|DefinitionNamePredicate
argument_list|(
name|oakName
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getNamedPropertyDefinitions
parameter_list|(
name|String
name|oakName
parameter_list|)
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
name|DefinitionNamePredicate
argument_list|(
name|oakName
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|NodeTypeConstants
operator|.
name|RESIDUAL_NAME
operator|.
name|equals
argument_list|(
name|nodeDefinition
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|NodeTypeConstants
operator|.
name|RESIDUAL_NAME
operator|.
name|equals
argument_list|(
name|propertyDefinition
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|checkAddChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeType
name|nodeType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeDefinition
name|definition
init|=
name|getDefinition
argument_list|(
name|name
argument_list|,
name|nodeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No matching node definition found for "
operator|+
name|name
argument_list|)
throw|;
block|}
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
name|nodeType
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|definition
operator|.
name|getDeclaringNodeType
argument_list|()
operator|.
name|canAddChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Cannot add node '"
operator|+
name|name
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|definition
operator|.
name|getDeclaringNodeType
argument_list|()
operator|.
name|canAddChildNode
argument_list|(
name|name
argument_list|,
name|nodeType
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
literal|"Cannot add node '"
operator|+
name|name
operator|+
literal|"' of type '"
operator|+
name|nodeType
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkRemoveNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeType
name|nodeType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeDefinition
name|definition
init|=
name|getDefinition
argument_list|(
name|name
argument_list|,
name|nodeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No matching node definition found for "
operator|+
name|name
argument_list|)
throw|;
block|}
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
name|canRemoveNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Cannot remove node '"
operator|+
name|name
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
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
name|tree
operator|.
name|getChild
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
name|ntMgr
operator|.
name|getDefinition
argument_list|(
name|nodeTypes
argument_list|,
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
specifier|private
name|NodeDefinition
name|getDefinition
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|NodeType
name|nodeType
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
comment|// FIXME: ugly hack to workaround sns-hack that was used to map sns-item definitions with node types.
name|String
name|nameToCheck
init|=
name|nodeName
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|startsWith
argument_list|(
literal|"jcr:childNodeDefinition"
argument_list|)
operator|&&
operator|!
name|nodeName
operator|.
name|equals
argument_list|(
literal|"jcr:childNodeDefinition"
argument_list|)
condition|)
block|{
name|nameToCheck
operator|=
name|nodeName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|"jcr:childNodeDefinition"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodeName
operator|.
name|startsWith
argument_list|(
literal|"jcr:propertyDefinition"
argument_list|)
operator|&&
operator|!
name|nodeName
operator|.
name|equals
argument_list|(
literal|"jcr:propertyDefinition"
argument_list|)
condition|)
block|{
name|nameToCheck
operator|=
name|nodeName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|"jcr:propertyDefinition"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ntMgr
operator|.
name|getDefinition
argument_list|(
name|nodeTypes
argument_list|,
name|nameToCheck
argument_list|,
name|nodeType
argument_list|)
return|;
block|}
specifier|private
class|class
name|DefinitionNamePredicate
implements|implements
name|Predicate
argument_list|<
name|ItemDefinition
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|oakName
decl_stmt|;
name|DefinitionNamePredicate
parameter_list|(
name|String
name|oakName
parameter_list|)
block|{
name|this
operator|.
name|oakName
operator|=
name|oakName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|ItemDefinition
name|definition
parameter_list|)
block|{
return|return
name|definition
operator|instanceof
name|ItemDefinitionImpl
operator|&&
operator|(
operator|(
name|ItemDefinitionImpl
operator|)
name|definition
operator|)
operator|.
name|getOakName
argument_list|()
operator|.
name|equals
argument_list|(
name|oakName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

