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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_CHILDNODEDEFINITION
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
name|JCR_NAME
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
name|JCR_NODETYPENAME
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
name|JCR_PROPERTYDEFINITION
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
name|JCR_REQUIREDPRIMARYTYPES
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
name|JCR_SYSTEM
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
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
name|NODE_TYPES_PATH
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
name|Validator
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
name|NodeBuilder
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

begin_comment
comment|/**  * Editor that validates the consistency of the in-content node type registry  * under {@code /jcr:system/jcr:nodeTypes} and maintains the access-optimized  * version under {@code /jcr:system/oak:nodeTypes}.  *  *<ul>  *<li>validate new definitions</li>  *<li>detect collisions,</li>  *<li>prevent circular inheritance,</li>  *<li>reject modifications to definitions that render existing content invalid,</li>  *<li>prevent un-registration of built-in node types.</li>  *</ul>  */
end_comment

begin_class
class|class
name|RegistrationEditor
extends|extends
name|DefaultEditor
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|changedTypes
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|removedTypes
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|RegistrationEditor
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|validateAndCompile
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
name|NodeBuilder
name|types
init|=
name|builder
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|child
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|NODE_TYPES_PATH
operator|+
literal|"/"
operator|+
name|name
decl_stmt|;
name|NodeBuilder
name|type
init|=
name|types
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// - jcr:nodeTypeName (NAME) protected mandatory
name|PropertyState
name|nodeTypeName
init|=
name|after
operator|.
name|getProperty
argument_list|(
name|JCR_NODETYPENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeTypeName
operator|==
literal|null
operator|||
operator|!
name|name
operator|.
name|equals
argument_list|(
name|nodeTypeName
operator|.
name|getValue
argument_list|(
name|NAME
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Unexpected "
operator|+
name|JCR_NODETYPENAME
operator|+
literal|" in "
operator|+
name|path
argument_list|)
throw|;
block|}
comment|// - jcr:supertypes (NAME) protected multiple
name|PropertyState
name|supertypes
init|=
name|after
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
name|value
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
operator|!
name|types
operator|.
name|hasChildNode
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Missing supertype "
operator|+
name|value
operator|+
literal|" in "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
name|type
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:nodeType"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|type
operator|.
name|removeNode
argument_list|(
literal|"oak:namedPropertyDefinitions"
argument_list|)
expr_stmt|;
name|type
operator|.
name|removeNode
argument_list|(
literal|"oak:residualPropertyDefinitions"
argument_list|)
expr_stmt|;
name|type
operator|.
name|removeNode
argument_list|(
literal|"oak:namedChildNodeDefinitions"
argument_list|)
expr_stmt|;
name|type
operator|.
name|removeNode
argument_list|(
literal|"oak:residualChildNodeDefinitions"
argument_list|)
expr_stmt|;
comment|// + jcr:propertyDefinition (nt:propertyDefinition)
comment|//   = nt:propertyDefinition protected sns
comment|// + jcr:childNodeDefinition (nt:childNodeDefinition)
comment|//   = nt:childNodeDefinition protected sns
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|after
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|childName
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|childName
operator|.
name|startsWith
argument_list|(
name|JCR_PROPERTYDEFINITION
argument_list|)
condition|)
block|{
name|processPropertyDefinition
argument_list|(
name|type
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|childName
operator|.
name|startsWith
argument_list|(
name|JCR_CHILDNODEDEFINITION
argument_list|)
condition|)
block|{
name|processChildNodeDefinition
argument_list|(
name|types
argument_list|,
name|type
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|processPropertyDefinition
parameter_list|(
name|NodeBuilder
name|type
parameter_list|,
name|NodeState
name|definition
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// - jcr:name (NAME) protected
name|PropertyState
name|name
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|JCR_NAME
argument_list|)
decl_stmt|;
name|NodeBuilder
name|definitions
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|String
name|escapedName
init|=
name|name
operator|.
name|getValue
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|escapedName
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
name|escapedName
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
name|escapedName
argument_list|)
condition|)
block|{
name|escapedName
operator|=
literal|"oak:uuid"
expr_stmt|;
block|}
name|definitions
operator|=
name|type
operator|.
name|child
argument_list|(
literal|"oak:namedPropertyDefinitions"
argument_list|)
expr_stmt|;
name|definitions
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:namedPropertyDefinitions"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|definitions
operator|=
name|definitions
operator|.
name|child
argument_list|(
name|escapedName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|definitions
operator|=
name|type
operator|.
name|child
argument_list|(
literal|"oak:residualPropertyDefinitions"
argument_list|)
expr_stmt|;
block|}
name|definitions
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:propertyDefinitions"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
comment|// - jcr:requiredType (STRING) protected mandatory
comment|//< 'STRING', 'URI', 'BINARY', 'LONG', 'DOUBLE',
comment|//   'DECIMAL', 'BOOLEAN', 'DATE', 'NAME', 'PATH',
comment|//   'REFERENCE', 'WEAKREFERENCE', 'UNDEFINED'
name|String
name|key
init|=
literal|"UNDEFINED"
decl_stmt|;
name|PropertyState
name|requiredType
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
name|requiredType
operator|!=
literal|null
condition|)
block|{
name|key
operator|=
name|requiredType
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
expr_stmt|;
block|}
comment|// - jcr:multiple (BOOLEAN) protected mandatory
name|PropertyState
name|multiple
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|JCR_MULTIPLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|multiple
operator|!=
literal|null
operator|&&
name|multiple
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"BINARY"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|key
operator|=
literal|"BINARIES"
expr_stmt|;
block|}
else|else
block|{
name|key
operator|=
name|key
operator|+
literal|"S"
expr_stmt|;
block|}
block|}
name|definitions
operator|.
name|setNode
argument_list|(
name|key
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|processChildNodeDefinition
parameter_list|(
name|NodeBuilder
name|types
parameter_list|,
name|NodeBuilder
name|type
parameter_list|,
name|NodeState
name|definition
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// - jcr:name (NAME) protected
name|PropertyState
name|name
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|JCR_NAME
argument_list|)
decl_stmt|;
name|NodeBuilder
name|definitions
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|definitions
operator|=
name|type
operator|.
name|child
argument_list|(
literal|"oak:namedChildNodeDefinitions"
argument_list|)
expr_stmt|;
name|definitions
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:namedChildNodeDefinitions"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|definitions
operator|=
name|definitions
operator|.
name|child
argument_list|(
name|name
operator|.
name|getValue
argument_list|(
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|definitions
operator|=
name|type
operator|.
name|child
argument_list|(
literal|"oak:residualChildNodeDefinitions"
argument_list|)
expr_stmt|;
block|}
name|definitions
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:childNodeDefinitions"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
comment|// - jcr:requiredPrimaryTypes (NAME)
comment|//   = 'nt:base' protected mandatory multiple
name|PropertyState
name|requiredTypes
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|JCR_REQUIREDPRIMARYTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|requiredTypes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|key
range|:
name|requiredTypes
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|types
operator|.
name|hasChildNode
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Unknown required primary type "
operator|+
name|key
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|definitions
operator|.
name|hasChildNode
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|definitions
operator|.
name|setNode
argument_list|(
name|key
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Updates the {@link #changedTypes} set to contain also all subtypes      * that may have been affected by the content changes even if they haven't      * been directly modified.      *      * @param types {@code /jcr:system/jcr:nodeTypes} after the changes      */
specifier|private
name|void
name|findAllAffectedTypes
parameter_list|(
name|NodeState
name|types
parameter_list|)
block|{
name|Queue
argument_list|<
name|String
argument_list|>
name|queue
init|=
name|Queues
operator|.
name|newArrayDeque
argument_list|(
name|changedTypes
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
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
comment|// TODO: We should be able to do this with just one pass
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|types
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|type
init|=
name|entry
operator|.
name|getNodeState
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
name|superName
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
name|name
operator|.
name|equals
argument_list|(
name|superName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|changedTypes
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
comment|/**      * Verifies that none of the remaining node types still references      * one of the removed types.      *      * @param types {@code /jcr:system/jcr:nodeTypes} after the changes      * @throws CommitFailedException if a removed type is still referenced      */
specifier|private
name|void
name|checkTypeReferencesToRemovedTypes
parameter_list|(
name|NodeState
name|types
parameter_list|)
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|types
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|type
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Are there any supertype references to removed types?
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
name|superName
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
name|removedTypes
operator|.
name|contains
argument_list|(
name|superName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Removed type "
operator|+
name|superName
operator|+
literal|" is still referenced as a supertype of "
operator|+
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|// Are there any child node definition references to removed types?
for|for
control|(
name|ChildNodeEntry
name|childEntry
range|:
name|types
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|childName
init|=
name|childEntry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|childName
operator|.
name|startsWith
argument_list|(
name|JCR_CHILDNODEDEFINITION
argument_list|)
condition|)
block|{
name|NodeState
name|definition
init|=
name|childEntry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|PropertyState
name|requiredTypes
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
name|requiredTypes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|required
range|:
name|requiredTypes
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
control|)
block|{
if|if
condition|(
name|removedTypes
operator|.
name|contains
argument_list|(
name|required
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Removed type "
operator|+
name|required
operator|+
literal|" is still referenced as a required "
operator|+
literal|" primary child node type in "
operator|+
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
comment|//------------------------------------------------------------< Editor>--
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
if|if
condition|(
operator|!
name|removedTypes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|checkTypeReferencesToRemovedTypes
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|changedTypes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|findAllAffectedTypes
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|changedTypes
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|removedTypes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// TODO: Find and re-validate any nodes in the repository that
comment|// refer to any of the changed (or removed) node types.
block|}
block|}
annotation|@
name|Override
specifier|public
name|Validator
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
name|validateAndCompile
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
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
name|validateAndCompile
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|changedTypes
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|removedTypes
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

