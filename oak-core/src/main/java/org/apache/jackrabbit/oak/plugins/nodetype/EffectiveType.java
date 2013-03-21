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
name|JCR_MANDATORY
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
name|spi
operator|.
name|state
operator|.
name|ChildNodeEntry
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

begin_class
class|class
name|EffectiveType
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|NodeState
argument_list|>
name|types
decl_stmt|;
name|EffectiveType
parameter_list|(
name|List
argument_list|<
name|NodeState
argument_list|>
name|types
parameter_list|)
block|{
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
name|void
name|checkMandatoryItems
parameter_list|(
name|NodeState
name|node
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
for|for
control|(
name|NodeState
name|type
range|:
name|types
control|)
block|{
name|NodeState
name|properties
init|=
name|type
operator|.
name|getChildNode
argument_list|(
literal|"oak:namedPropertyDefinitions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|properties
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"oak:primaryType"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|name
operator|=
name|JCR_PRIMARYTYPE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"oak:mixinTypes"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|name
operator|=
name|JCR_MIXINTYPES
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"oak:uuid"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|name
operator|=
name|JCR_UUID
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|==
literal|null
operator|&&
name|isMandatory
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Missing mandatory property "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
block|}
name|NodeState
name|childNodes
init|=
name|type
operator|.
name|getChildNode
argument_list|(
literal|"oak:namedChildNodeDefinitions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|childNodes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|childNodes
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|node
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
operator|&&
name|isMandatory
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Missing mandatory child node "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
comment|/**      * Finds a matching definition for a property with the given name and type.      *      * @param property modified property      * @return matching property definition      * @throws ConstraintViolationException if a matching definition was not found      */
annotation|@
name|Nonnull
name|NodeState
name|getDefinition
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|String
name|propertyName
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Type
argument_list|<
name|?
argument_list|>
name|propertyType
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|escapedName
decl_stmt|;
if|if
condition|(
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|escapedName
operator|=
literal|"oak:primaryType"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|JCR_MIXINTYPES
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|escapedName
operator|=
literal|"oak:mixinTypes"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|JCR_UUID
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|escapedName
operator|=
literal|"oak:uuid"
expr_stmt|;
block|}
else|else
block|{
name|escapedName
operator|=
name|propertyName
expr_stmt|;
block|}
name|String
name|definedType
init|=
name|getTypeKey
argument_list|(
name|propertyType
argument_list|)
decl_stmt|;
name|String
name|undefinedType
decl_stmt|;
if|if
condition|(
name|propertyType
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|undefinedType
operator|=
literal|"UNDEFINEDS"
expr_stmt|;
block|}
else|else
block|{
name|undefinedType
operator|=
literal|"UNDEFINED"
expr_stmt|;
block|}
comment|// Find matching named property definition
for|for
control|(
name|NodeState
name|type
range|:
name|types
control|)
block|{
name|NodeState
name|named
init|=
name|type
operator|.
name|getChildNode
argument_list|(
literal|"oak:namedPropertyDefinitions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|named
operator|!=
literal|null
condition|)
block|{
name|NodeState
name|definitions
init|=
name|named
operator|.
name|getChildNode
argument_list|(
name|escapedName
argument_list|)
decl_stmt|;
if|if
condition|(
name|definitions
operator|!=
literal|null
condition|)
block|{
name|NodeState
name|definition
init|=
name|definitions
operator|.
name|getChildNode
argument_list|(
name|definedType
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|==
literal|null
condition|)
block|{
name|definition
operator|=
name|definitions
operator|.
name|getChildNode
argument_list|(
name|undefinedType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|definition
operator|!=
literal|null
condition|)
block|{
return|return
name|definition
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No matching definition found for property "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|// Find matching residual property definition
for|for
control|(
name|NodeState
name|type
range|:
name|types
control|)
block|{
name|NodeState
name|residual
init|=
name|type
operator|.
name|getChildNode
argument_list|(
literal|"oak:residualPropertyDefinitions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|residual
operator|!=
literal|null
condition|)
block|{
name|NodeState
name|definition
init|=
name|residual
operator|.
name|getChildNode
argument_list|(
name|definedType
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|==
literal|null
condition|)
block|{
name|definition
operator|=
name|residual
operator|.
name|getChildNode
argument_list|(
name|undefinedType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|definition
operator|!=
literal|null
condition|)
block|{
return|return
name|definition
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"No matching definition found for property "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
comment|/**      * Finds a matching definition for a child node with the given name and      * types.      *      * @param nodeName child node name      * @param nodeType effective types of the child node      * @return matching child node definition      * @throws ConstraintViolationException if a matching definition was not found      */
annotation|@
name|Nonnull
name|NodeState
name|getDefinition
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|nodeType
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|boolean
name|sns
init|=
literal|false
decl_stmt|;
name|int
name|n
init|=
name|nodeName
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|3
operator|&&
name|nodeName
operator|.
name|charAt
argument_list|(
name|n
operator|-
literal|1
argument_list|)
operator|==
literal|']'
condition|)
block|{
name|int
name|i
init|=
name|n
operator|-
literal|2
decl_stmt|;
while|while
condition|(
name|i
operator|>
literal|1
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|nodeName
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|i
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|nodeName
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'['
condition|)
block|{
name|nodeName
operator|=
name|nodeName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|sns
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// Find matching named child node definition
for|for
control|(
name|NodeState
name|type
range|:
name|types
control|)
block|{
name|NodeState
name|named
init|=
name|type
operator|.
name|getChildNode
argument_list|(
literal|"oak:namedChildNodeDefinitions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|named
operator|!=
literal|null
condition|)
block|{
name|NodeState
name|definitions
init|=
name|named
operator|.
name|getChildNode
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|definitions
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|typeName
range|:
name|nodeType
control|)
block|{
name|NodeState
name|definition
init|=
name|definitions
operator|.
name|getChildNode
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|!=
literal|null
condition|)
block|{
return|return
name|definition
return|;
block|}
block|}
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Incorrect node type of child node "
operator|+
name|nodeName
argument_list|)
throw|;
block|}
block|}
block|}
comment|// Find matching residual child node definition
for|for
control|(
name|NodeState
name|type
range|:
name|types
control|)
block|{
name|NodeState
name|residual
init|=
name|type
operator|.
name|getChildNode
argument_list|(
literal|"oak:residualChildNodeDefinitions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|residual
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|typeName
range|:
name|nodeType
control|)
block|{
name|NodeState
name|definition
init|=
name|residual
operator|.
name|getChildNode
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|!=
literal|null
condition|)
block|{
return|return
name|definition
return|;
block|}
block|}
block|}
block|}
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Incorrect node type of child node "
operator|+
name|nodeName
argument_list|)
throw|;
block|}
comment|//-----------------------------------------------------------< private>--
specifier|private
name|boolean
name|isMandatory
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|definitions
parameter_list|)
block|{
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|definitions
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|definition
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|getBoolean
argument_list|(
name|definition
argument_list|,
name|JCR_MANDATORY
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
specifier|private
name|String
name|getTypeKey
parameter_list|(
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|BINARIES
condition|)
block|{
return|return
literal|"BINARIES"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|BINARY
condition|)
block|{
return|return
literal|"BINARY"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|BOOLEAN
condition|)
block|{
return|return
literal|"BOOLEAN"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|BOOLEANS
condition|)
block|{
return|return
literal|"BOOLEANS"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DATE
condition|)
block|{
return|return
literal|"DATE"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DATES
condition|)
block|{
return|return
literal|"DATES"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DECIMAL
condition|)
block|{
return|return
literal|"DECIMAL"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DECIMALS
condition|)
block|{
return|return
literal|"DECIMALS"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DOUBLE
condition|)
block|{
return|return
literal|"DOUBLE"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DOUBLES
condition|)
block|{
return|return
literal|"DOUBLES"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|LONG
condition|)
block|{
return|return
literal|"LONG"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|LONGS
condition|)
block|{
return|return
literal|"LONGS"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|NAME
condition|)
block|{
return|return
literal|"NAME"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|NAMES
condition|)
block|{
return|return
literal|"NAMES"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|PATH
condition|)
block|{
return|return
literal|"PATH"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|PATHS
condition|)
block|{
return|return
literal|"PATHS"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|REFERENCE
condition|)
block|{
return|return
literal|"REFERENCE"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|REFERENCES
condition|)
block|{
return|return
literal|"REFERENCES"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|STRING
condition|)
block|{
return|return
literal|"STRING"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|STRINGS
condition|)
block|{
return|return
literal|"STRINGS"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|URI
condition|)
block|{
return|return
literal|"URI"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|URIS
condition|)
block|{
return|return
literal|"URIS"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|WEAKREFERENCE
condition|)
block|{
return|return
literal|"WEAKREFERENCE"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|WEAKREFERENCES
condition|)
block|{
return|return
literal|"WEAKREFERENCES"
return|;
block|}
else|else
block|{
return|return
literal|"unknown"
return|;
block|}
block|}
block|}
end_class

end_unit

