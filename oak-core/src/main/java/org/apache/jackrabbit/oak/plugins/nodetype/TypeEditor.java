begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|JCR_REQUIREDTYPE
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
name|JCR_SUPERTYPES
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
name|JCR_VALUECONSTRAINTS
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
name|constraint
operator|.
name|Constraints
operator|.
name|valueConstraint
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
name|value
operator|.
name|ValueFactoryImpl
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
name|commit
operator|.
name|DefaultEditor
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
name|commit
operator|.
name|Editor
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
name|Queues
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

begin_comment
comment|/**  * Validator implementation that check JCR node type constraints.  *  * TODO: check protected properties and the structure they enforce. some of  *       those checks may have to go into separate validator classes. This class  *       should only perform checks based on node type information. E.g. it  *       cannot and should not check whether the value of the protected jcr:uuid  *       is unique.  */
end_comment

begin_class
class|class
name|TypeEditor
extends|extends
name|DefaultEditor
block|{
specifier|private
specifier|final
name|TypeEditor
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|types
decl_stmt|;
specifier|private
name|EffectiveType
name|effective
init|=
literal|null
decl_stmt|;
name|TypeEditor
parameter_list|(
name|NodeState
name|types
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|checkNotNull
argument_list|(
name|types
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TypeEditor
parameter_list|(
name|TypeEditor
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|parent
operator|.
name|types
expr_stmt|;
block|}
comment|/**      * Computes the effective type of the modified type.      */
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|names
init|=
name|computeEffectiveType
argument_list|(
name|after
argument_list|)
decl_stmt|;
comment|// find matching entry in the parent node's effective type
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|parent
operator|.
name|effective
operator|.
name|getDefinition
argument_list|(
name|nodeName
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// TODO: add any auto-created items that are still missing
comment|// verify the presence of all mandatory items
try|try
block|{
name|effective
operator|.
name|checkMandatoryItems
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|CommitFailedException
name|constraintViolation
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|CommitFailedException
argument_list|(
operator|new
name|ConstraintViolationException
argument_list|(
name|message
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
try|try
block|{
name|NodeState
name|definition
init|=
name|effective
operator|.
name|getDefinition
argument_list|(
name|after
argument_list|)
decl_stmt|;
name|checkValueConstraints
argument_list|(
name|definition
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
try|try
block|{
name|NodeState
name|definition
init|=
name|effective
operator|.
name|getDefinition
argument_list|(
name|after
argument_list|)
decl_stmt|;
name|checkValueConstraints
argument_list|(
name|definition
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|TypeEditor
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|TypeEditor
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|getBoolean
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|BOOLEAN
operator|&&
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEAN
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------------< private>--
specifier|private
name|void
name|checkValueConstraints
parameter_list|(
name|NodeState
name|definition
parameter_list|,
name|PropertyState
name|property
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|property
operator|.
name|count
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|PropertyState
name|constraints
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|JCR_VALUECONSTRAINTS
argument_list|)
decl_stmt|;
if|if
condition|(
name|constraints
operator|==
literal|null
operator|||
name|constraints
operator|.
name|count
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|PropertyState
name|required
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|JCR_REQUIREDTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|required
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|type
decl_stmt|;
name|String
name|value
init|=
name|required
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"BINARY"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|BINARY
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"BOOLEAN"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|BOOLEAN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"DATE"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|DATE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"DECIMAL"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|DECIMAL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"DOUBLE"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|DOUBLE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"LONG"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|LONG
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"NAME"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|NAME
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"PATH"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|PATH
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"REFERENCE"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|REFERENCE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"STRING"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|STRING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"URI"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|URI
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"WEAKREFERENCE"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|type
operator|=
name|PropertyType
operator|.
name|WEAKREFERENCE
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
for|for
control|(
name|String
name|constraint
range|:
name|constraints
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
name|Predicate
argument_list|<
name|Value
argument_list|>
name|predicate
init|=
name|valueConstraint
argument_list|(
name|type
argument_list|,
name|constraint
argument_list|)
decl_stmt|;
for|for
control|(
name|Value
name|v
range|:
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|property
argument_list|,
literal|null
argument_list|)
control|)
block|{
if|if
condition|(
name|predicate
operator|.
name|apply
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
throw|throw
name|constraintViolation
argument_list|(
literal|"Value constraint violation"
argument_list|)
throw|;
block|}
comment|/**      * Collects the primary and mixin types and all related supertypes      * of the given node and places them in the {@link #effective} list      * of effective node type definitions.      *      * @param node node state      * @return names of the types that make up the effective type      * @throws CommitFailedException if the effective node type is invalid      */
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|computeEffectiveType
parameter_list|(
name|NodeState
name|node
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|List
argument_list|<
name|NodeState
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Sets
operator|.
name|newLinkedHashSet
argument_list|()
decl_stmt|;
comment|// primary type
name|PropertyState
name|primary
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|primary
operator|!=
literal|null
operator|&&
name|primary
operator|.
name|getType
argument_list|()
operator|==
name|NAME
condition|)
block|{
name|String
name|name
init|=
name|primary
operator|.
name|getValue
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|NodeState
name|type
init|=
name|types
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
name|constraintViolation
argument_list|(
literal|"Primary node type "
operator|+
name|name
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
name|JCR_ISMIXIN
argument_list|)
condition|)
block|{
throw|throw
name|constraintViolation
argument_list|(
literal|"Can not use mixin type "
operator|+
name|name
operator|+
literal|" as primary"
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
name|constraintViolation
argument_list|(
literal|"Can not use abstract type "
operator|+
name|name
operator|+
literal|" as primary"
argument_list|)
throw|;
block|}
name|list
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
comment|// mixin types
name|PropertyState
name|mixins
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixins
operator|!=
literal|null
operator|&&
name|mixins
operator|.
name|getType
argument_list|()
operator|==
name|NAMES
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|mixins
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
control|)
block|{
if|if
condition|(
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeState
name|type
init|=
name|types
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
name|constraintViolation
argument_list|(
literal|"Mixin node type "
operator|+
name|name
operator|+
literal|" does not exist"
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
name|constraintViolation
argument_list|(
literal|"Can not use primary type "
operator|+
name|name
operator|+
literal|" as mixin"
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
name|constraintViolation
argument_list|(
literal|"Can not use abstract type "
operator|+
name|name
operator|+
literal|" as mixin"
argument_list|)
throw|;
block|}
name|list
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// supertypes
name|Queue
argument_list|<
name|NodeState
argument_list|>
name|queue
init|=
name|Queues
operator|.
name|newArrayDeque
argument_list|(
name|list
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
name|NodeState
name|type
init|=
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
name|PropertyState
name|supertypes
init|=
name|type
operator|.
name|getProperty
argument_list|(
name|JCR_SUPERTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|supertypes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|supertypes
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
control|)
block|{
if|if
condition|(
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeState
name|supertype
init|=
name|types
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|supertype
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|supertype
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|supertype
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: ignore/warning/error?
block|}
block|}
block|}
block|}
block|}
comment|// always include nt:base
if|if
condition|(
name|names
operator|.
name|add
argument_list|(
name|NT_BASE
argument_list|)
condition|)
block|{
name|NodeState
name|base
init|=
name|types
operator|.
name|getChildNode
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: ignore/warning/error?
block|}
block|}
name|effective
operator|=
operator|new
name|EffectiveType
argument_list|(
name|list
argument_list|)
expr_stmt|;
return|return
name|names
return|;
block|}
block|}
end_class

end_unit

