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
name|name
package|;
end_package

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
name|NodeState
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

begin_class
class|class
name|NameValidator
implements|implements
name|Validator
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|prefixes
decl_stmt|;
specifier|public
name|NameValidator
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|prefixes
parameter_list|)
block|{
name|this
operator|.
name|prefixes
operator|=
name|prefixes
expr_stmt|;
block|}
specifier|protected
name|void
name|checkValidName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|prefix
init|=
literal|null
decl_stmt|;
name|String
name|local
init|=
name|name
decl_stmt|;
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|==
name|name
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Local name most not be empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|colon
operator|!=
operator|-
literal|1
condition|)
block|{
name|prefix
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
expr_stmt|;
name|local
operator|=
name|name
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|prefix
operator|==
literal|null
operator|||
operator|!
name|prefixes
operator|.
name|contains
argument_list|(
name|prefix
argument_list|)
operator|)
operator|||
operator|!
name|isValidLocalName
argument_list|(
name|local
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Invalid name: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isValidLocalName
parameter_list|(
name|String
name|local
parameter_list|)
block|{
if|if
condition|(
literal|"."
operator|.
name|equals
argument_list|(
name|local
argument_list|)
operator|||
literal|".."
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|local
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|local
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"/:[]|*"
operator|.
name|indexOf
argument_list|(
name|ch
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// TODO: XMLChar check
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|//-------------------------------------------------------< NodeValidator>
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
name|checkValidName
argument_list|(
name|after
operator|.
name|getName
argument_list|()
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
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
comment|// do nothing
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
name|checkValidName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|this
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
return|return
name|this
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
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

