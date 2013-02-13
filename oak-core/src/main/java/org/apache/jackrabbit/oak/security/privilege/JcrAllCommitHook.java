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
name|security
operator|.
name|privilege
package|;
end_package

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
name|plugins
operator|.
name|memory
operator|.
name|MemoryPropertyBuilder
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
name|CommitHook
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
name|EmptyNodeStateDiff
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
name|PropertyBuilder
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
name|Text
import|;
end_import

begin_comment
comment|/**  * JcrAllCommitHook is responsible for updating the jcr:all privilege definition  * upon successful registration of a new privilege.  */
end_comment

begin_class
specifier|public
class|class
name|JcrAllCommitHook
implements|implements
name|CommitHook
implements|,
name|PrivilegeConstants
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
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
name|NodeBuilder
name|builder
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|PrivilegeDiff
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
specifier|final
class|class
name|PrivilegeDiff
extends|extends
name|EmptyNodeStateDiff
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ROOT_PATH
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|nodeBuilder
decl_stmt|;
specifier|private
name|PrivilegeDiff
parameter_list|(
name|PrivilegeDiff
name|parentDiff
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|NodeBuilder
name|nodeBuilder
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
operator|(
name|nodeName
operator|==
literal|null
operator|)
condition|?
name|ROOT_PATH
else|:
name|parentDiff
operator|.
name|path
operator|+
literal|'/'
operator|+
name|nodeName
expr_stmt|;
name|this
operator|.
name|nodeBuilder
operator|=
name|nodeBuilder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|PRIVILEGES_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|&&
operator|!
name|JCR_ALL
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// a new privilege was registered -> update the jcr:all privilege
name|NodeBuilder
name|jcrAll
init|=
name|nodeBuilder
operator|.
name|child
argument_list|(
name|JCR_ALL
argument_list|)
decl_stmt|;
name|PropertyState
name|aggregates
init|=
name|jcrAll
operator|.
name|getProperty
argument_list|(
name|REP_AGGREGATES
argument_list|)
decl_stmt|;
comment|// FIXME: remove usage of MemoryPropertyBuilder (OAK-372)
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|propertyBuilder
decl_stmt|;
if|if
condition|(
name|aggregates
operator|==
literal|null
condition|)
block|{
name|propertyBuilder
operator|=
name|MemoryPropertyBuilder
operator|.
name|array
argument_list|(
name|Type
operator|.
name|NAME
argument_list|,
name|REP_AGGREGATES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|propertyBuilder
operator|=
name|MemoryPropertyBuilder
operator|.
name|copy
argument_list|(
name|Type
operator|.
name|NAME
argument_list|,
name|aggregates
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|propertyBuilder
operator|.
name|hasValue
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|propertyBuilder
operator|.
name|addValue
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|jcrAll
operator|.
name|setProperty
argument_list|(
name|propertyBuilder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// update the privilege bits of the jcr:all in case the new
comment|// privilege isn't an aggregate
if|if
condition|(
name|after
operator|.
name|getProperty
argument_list|(
name|REP_AGGREGATES
argument_list|)
operator|==
literal|null
condition|)
block|{
name|PrivilegeBits
name|bits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|after
operator|.
name|getProperty
argument_list|(
name|REP_BITS
argument_list|)
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|all
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|jcrAll
operator|.
name|getProperty
argument_list|(
name|REP_BITS
argument_list|)
argument_list|)
decl_stmt|;
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|all
argument_list|)
operator|.
name|add
argument_list|(
name|bits
argument_list|)
operator|.
name|writeTo
argument_list|(
name|jcrAll
argument_list|,
name|JCR_ALL
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
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
if|if
condition|(
name|ROOT_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
name|Text
operator|.
name|isDescendant
argument_list|(
name|path
argument_list|,
name|PRIVILEGES_PATH
argument_list|)
condition|)
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|PrivilegeDiff
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|nodeBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

