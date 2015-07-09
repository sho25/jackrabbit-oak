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
name|commit
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
name|MIX_REP_MERGE_CONFLICT
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
name|spi
operator|.
name|commit
operator|.
name|DefaultValidator
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
name|ConflictType
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
name|Joiner
import|;
end_import

begin_comment
comment|/**  * {@link Validator} which checks the presence of conflict markers  * in the tree in fails the commit if any are found.  *  * @see AnnotatingConflictHandler  */
end_comment

begin_class
specifier|public
class|class
name|ConflictValidator
extends|extends
name|DefaultValidator
block|{
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConflictValidator
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Current processed path, or null if the debug log is not enabled at the      * beginning of the call. The null check will also be used to verify if a      * debug log will be needed or not      */
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|NodeState
name|after
decl_stmt|;
annotation|@
name|Deprecated
specifier|public
name|ConflictValidator
parameter_list|(
name|Tree
name|parentAfter
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
block|}
name|ConflictValidator
parameter_list|()
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|path
operator|=
literal|"/"
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|path
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|ConflictValidator
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|path
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isLogEnabled
parameter_list|()
block|{
return|return
name|path
operator|!=
literal|null
return|;
block|}
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
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
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
name|this
operator|.
name|after
operator|=
literal|null
expr_stmt|;
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
name|failOnMergeConflict
argument_list|(
name|after
argument_list|)
expr_stmt|;
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
name|failOnMergeConflict
argument_list|(
name|after
argument_list|)
expr_stmt|;
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
block|{
return|return
operator|new
name|ConflictValidator
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
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
block|{
return|return
operator|new
name|ConflictValidator
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
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
block|{
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|failOnMergeConflict
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
assert|assert
name|property
operator|.
name|isArray
argument_list|()
assert|;
for|for
control|(
name|String
name|v
range|:
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
if|if
condition|(
name|MIX_REP_MERGE_CONFLICT
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
name|CommitFailedException
name|ex
init|=
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|STATE
argument_list|,
literal|1
argument_list|,
literal|"Unresolved conflicts in "
operator|+
name|path
argument_list|)
decl_stmt|;
comment|//Conflict details are not made part of ExceptionMessage instead they are
comment|//logged. This to avoid exposing property details to the caller as it might not have
comment|//permission to access it
if|if
condition|(
name|isLogEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|getConflictMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
block|}
block|}
block|}
specifier|private
name|String
name|getConflictMessage
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Commit failed due to unresolved conflicts in "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" = {"
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|conflict
range|:
name|after
operator|.
name|getChildNode
argument_list|(
name|NodeTypeConstants
operator|.
name|REP_OURS
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|ConflictType
name|ct
init|=
name|ConflictType
operator|.
name|fromName
argument_list|(
name|conflict
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|NodeState
name|node
init|=
name|conflict
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ct
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" = {"
argument_list|)
expr_stmt|;
if|if
condition|(
name|ct
operator|.
name|effectsNode
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|getChildNodeNamesAsString
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|PropertyState
name|ps
range|:
name|node
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|PropertyState
name|ours
init|=
literal|null
decl_stmt|,
name|theirs
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|ct
condition|)
block|{
case|case
name|DELETE_CHANGED_PROPERTY
case|:
name|ours
operator|=
literal|null
expr_stmt|;
name|theirs
operator|=
name|ps
expr_stmt|;
break|break;
case|case
name|ADD_EXISTING_PROPERTY
case|:
case|case
name|CHANGE_CHANGED_PROPERTY
case|:
name|ours
operator|=
name|ps
expr_stmt|;
name|theirs
operator|=
name|after
operator|.
name|getProperty
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|CHANGE_DELETED_PROPERTY
case|:
name|ours
operator|=
name|ps
expr_stmt|;
name|theirs
operator|=
literal|null
expr_stmt|;
break|break;
block|}
name|sb
operator|.
name|append
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" = {"
argument_list|)
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|ours
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|theirs
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"},"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|getChildNodeNamesAsString
parameter_list|(
name|NodeState
name|ns
parameter_list|)
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|ns
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|PropertyState
name|ps
parameter_list|)
block|{
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
return|return
literal|"<N/A>"
return|;
block|}
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|ps
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
literal|"<ARRAY>"
return|;
block|}
if|if
condition|(
name|Type
operator|.
name|BINARY
operator|==
name|type
condition|)
block|{
return|return
literal|"<BINARY>"
return|;
block|}
name|String
name|value
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
comment|//Trim the value so as to not blowup diff message
if|if
condition|(
name|Type
operator|.
name|STRING
operator|==
name|type
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|10
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
operator|+
literal|"..."
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

