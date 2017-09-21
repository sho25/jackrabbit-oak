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
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|MISSING_NODE
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
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
name|NodeType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|namepath
operator|.
name|NamePathMapper
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
name|RootFactory
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
name|CommitInfo
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
name|commit
operator|.
name|EditorDiff
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
name|EditorProvider
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
name|VisibleEditor
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

begin_class
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
name|EditorProvider
operator|.
name|class
argument_list|)
specifier|public
class|class
name|TypeEditorProvider
implements|implements
name|EditorProvider
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TypeEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|strict
decl_stmt|;
specifier|public
name|TypeEditorProvider
parameter_list|(
name|boolean
name|strict
parameter_list|)
block|{
name|this
operator|.
name|strict
operator|=
name|strict
expr_stmt|;
block|}
specifier|public
name|TypeEditorProvider
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|getRootEditor
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|beforeTypes
init|=
name|before
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
name|NodeState
name|afterTypes
init|=
name|after
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
name|String
name|primary
init|=
name|after
operator|.
name|getName
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixins
init|=
name|after
operator|.
name|getNames
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
name|TypeRegistration
name|registration
init|=
operator|new
name|TypeRegistration
argument_list|()
decl_stmt|;
name|afterTypes
operator|.
name|compareAgainstBaseState
argument_list|(
name|beforeTypes
argument_list|,
name|registration
argument_list|)
expr_stmt|;
if|if
condition|(
name|registration
operator|.
name|isModified
argument_list|()
condition|)
block|{
name|ReadOnlyNodeTypeManager
name|ntBefore
init|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|before
argument_list|)
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|ReadOnlyNodeTypeManager
name|ntAfter
init|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|after
argument_list|)
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|afterTypes
operator|=
name|registration
operator|.
name|apply
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|modifiedTypes
init|=
name|registration
operator|.
name|getModifiedTypes
argument_list|(
name|beforeTypes
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|modifiedTypes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|t
range|:
name|modifiedTypes
control|)
block|{
name|boolean
name|mod
init|=
operator|!
name|isTrivialChange
argument_list|(
name|ntBefore
argument_list|,
name|ntAfter
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|modified
operator|=
name|modified
operator|||
name|mod
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|modified
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Node type changes: "
operator|+
name|modifiedTypes
operator|+
literal|" appear to be trivial, repository will not be scanned"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// Some node types were modified, so scan the repository
comment|// to make sure that the modified definitions still apply.
name|Editor
name|editor
init|=
operator|new
name|VisibleEditor
argument_list|(
operator|new
name|TypeEditor
argument_list|(
name|strict
argument_list|,
name|modifiedTypes
argument_list|,
name|afterTypes
argument_list|,
name|primary
argument_list|,
name|mixins
argument_list|,
name|builder
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Node type changes: "
operator|+
name|modifiedTypes
operator|+
literal|" appear not to be trivial, starting repository scan"
argument_list|)
expr_stmt|;
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|editor
argument_list|,
name|MISSING_NODE
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Node type changes: "
operator|+
name|modifiedTypes
operator|+
literal|"; repository scan took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"ms"
operator|+
operator|(
name|exception
operator|==
literal|null
condition|?
literal|""
else|:
literal|"; failed with "
operator|+
name|exception
operator|.
name|getMessage
argument_list|()
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
block|}
block|}
block|}
return|return
operator|new
name|VisibleEditor
argument_list|(
operator|new
name|TypeEditor
argument_list|(
name|strict
argument_list|,
literal|null
argument_list|,
name|afterTypes
argument_list|,
name|primary
argument_list|,
name|mixins
argument_list|,
name|builder
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isTrivialChange
parameter_list|(
name|ReadOnlyNodeTypeManager
name|ntBefore
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntAfter
parameter_list|,
name|String
name|nodeType
parameter_list|)
block|{
name|NodeType
name|nb
decl_stmt|,
name|na
decl_stmt|;
try|try
block|{
name|nb
operator|=
name|ntBefore
operator|.
name|getNodeType
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchNodeTypeException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|nodeType
operator|+
literal|" not present in 'before' state"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"getting node type"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
try|try
block|{
name|na
operator|=
name|ntAfter
operator|.
name|getNodeType
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchNodeTypeException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|nodeType
operator|+
literal|" was removed"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"getting node type"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|NodeTypeDefDiff
name|diff
init|=
name|NodeTypeDefDiff
operator|.
name|create
argument_list|(
name|nb
argument_list|,
name|na
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|diff
operator|.
name|isModified
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Node type "
operator|+
name|nodeType
operator|+
literal|" was not changed"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|diff
operator|.
name|isTrivial
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Node type change for "
operator|+
name|nodeType
operator|+
literal|" appears to be trivial"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Node type change for "
operator|+
name|nodeType
operator|+
literal|" requires repository scan: "
operator|+
name|diff
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

