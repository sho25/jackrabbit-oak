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
name|spi
operator|.
name|state
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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
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

begin_comment
comment|/**  * A {@code MoveDetector} is a validator that can detect certain move operations  * and reports these to the wrapped {@link MoveValidator} by calling  * {@link MoveValidator#move(String, String, NodeState)}. That method is called additional  * to {@link MoveValidator#childNodeAdded(String, NodeState)} for the destination of the move  * operation and {@link MoveValidator#childNodeDeleted(String, NodeState)} for the source of  * the move operation.  *<p>  * Detection of move operations relies on the presence of the {@link #SOURCE_PATH} property.  * New nodes with this property set have been moved from the path indicated by the value of the  * property to its current location.  *<p>  * Limitations:  *<ul>  *<li>Moving a moved node only reports one move from the original source to the final  *     target.</li>  *<li>Moving a transiently added node is not reported as a move operation but as an  *     add operation on the move target.</li>  *<li>Moving a child node of a transiently moved node is not reported as a move operation  *     but as an add operation on the move target.</li>  *<li>Moving a node back and forth to its original location is not reported at all.</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|MoveDetector
extends|extends
name|DefaultValidator
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SOURCE_PATH
init|=
literal|":source-path"
decl_stmt|;
specifier|private
specifier|final
name|MoveValidator
name|moveValidator
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|MoveDetector
parameter_list|(
name|MoveValidator
name|moveValidator
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|moveValidator
operator|=
name|moveValidator
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|MoveDetector
parameter_list|(
name|MoveValidator
name|moveValidator
parameter_list|)
block|{
name|this
argument_list|(
name|moveValidator
argument_list|,
literal|"/"
argument_list|)
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
name|moveValidator
operator|.
name|propertyAdded
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
name|moveValidator
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
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
throws|throws
name|CommitFailedException
block|{
name|moveValidator
operator|.
name|propertyDeleted
argument_list|(
name|before
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
throws|throws
name|CommitFailedException
block|{
name|PropertyState
name|sourceProperty
init|=
name|after
operator|.
name|getProperty
argument_list|(
name|SOURCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceProperty
operator|!=
literal|null
condition|)
block|{
name|String
name|sourcePath
init|=
name|sourceProperty
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
name|String
name|destPath
init|=
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|moveValidator
operator|.
name|move
argument_list|(
name|sourcePath
argument_list|,
name|destPath
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
name|MoveValidator
name|childDiff
init|=
name|moveValidator
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
return|return
name|childDiff
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|MoveDetector
argument_list|(
name|childDiff
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
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
throws|throws
name|CommitFailedException
block|{
name|MoveValidator
name|childDiff
init|=
name|moveValidator
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
return|return
name|childDiff
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|MoveDetector
argument_list|(
name|childDiff
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
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
throws|throws
name|CommitFailedException
block|{
name|MoveValidator
name|childDiff
init|=
name|moveValidator
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
return|return
name|childDiff
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|MoveDetector
argument_list|(
name|childDiff
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

