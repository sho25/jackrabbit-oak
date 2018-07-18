begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|util
operator|.
name|ISO8601
operator|.
name|parse
import|;
end_import

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
name|NodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Conflict Handler that merges concurrent updates to  * {@code org.apache.jackrabbit.JcrConstants.JCR_LASTMODIFIED} by picking the  * older of the 2 conflicting dates and  * {@code org.apache.jackrabbit.JcrConstants.JCR_CREATED} by picking the newer  * of the 2 conflicting dates.  */
end_comment

begin_class
specifier|public
class|class
name|JcrLastModifiedConflictHandler
extends|extends
name|DefaultThreeWayConflictHandler
block|{
specifier|public
name|JcrLastModifiedConflictHandler
parameter_list|()
block|{
name|super
argument_list|(
name|Resolution
operator|.
name|IGNORED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Resolution
name|addExistingProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
if|if
condition|(
name|isModifiedOrCreated
argument_list|(
name|ours
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|merge
argument_list|(
name|parent
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|MERGED
return|;
block|}
return|return
name|Resolution
operator|.
name|IGNORED
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Resolution
name|changeChangedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|,
name|PropertyState
name|base
parameter_list|)
block|{
if|if
condition|(
name|isModifiedOrCreated
argument_list|(
name|ours
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|merge
argument_list|(
name|parent
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|MERGED
return|;
block|}
return|return
name|Resolution
operator|.
name|IGNORED
return|;
block|}
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
name|Calendar
name|o
init|=
name|parse
argument_list|(
name|ours
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
decl_stmt|;
name|Calendar
name|t
init|=
name|parse
argument_list|(
name|theirs
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|JCR_CREATED
operator|.
name|equals
argument_list|(
name|ours
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|parent
operator|.
name|setProperty
argument_list|(
name|ours
operator|.
name|getName
argument_list|()
argument_list|,
name|pick
argument_list|(
name|o
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|.
name|setProperty
argument_list|(
name|ours
operator|.
name|getName
argument_list|()
argument_list|,
name|pick
argument_list|(
name|o
argument_list|,
name|t
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Calendar
name|pick
parameter_list|(
name|Calendar
name|a
parameter_list|,
name|Calendar
name|b
parameter_list|,
name|boolean
name|jcrCreated
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|before
argument_list|(
name|b
argument_list|)
condition|)
block|{
return|return
name|jcrCreated
condition|?
name|a
else|:
name|b
return|;
block|}
else|else
block|{
return|return
name|jcrCreated
condition|?
name|b
else|:
name|a
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isModifiedOrCreated
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|JCR_LASTMODIFIED
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|JCR_CREATED
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

