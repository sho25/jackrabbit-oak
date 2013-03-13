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
name|jcr
operator|.
name|delegate
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
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
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
name|ValueFormatException
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
name|TreeLocation
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
name|PropertyStates
import|;
end_import

begin_comment
comment|/**  * {@code PropertyDelegate} serve as internal representations of {@code Property}s.  * Most methods of this class throw an {@code InvalidItemStateException}  * exception if the instance is stale. An instance is stale if the underlying  * items does not exist anymore.  */
end_comment

begin_class
specifier|public
class|class
name|PropertyDelegate
extends|extends
name|ItemDelegate
block|{
specifier|public
name|PropertyDelegate
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|TreeLocation
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|sessionDelegate
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PropertyState
name|getPropertyState
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|PropertyState
name|p
init|=
name|getLocation
argument_list|()
operator|.
name|getProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|()
throw|;
block|}
return|return
name|p
return|;
block|}
specifier|public
name|boolean
name|isArray
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getPropertyState
argument_list|()
operator|.
name|isArray
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|PropertyState
name|getSingle
parameter_list|()
throws|throws
name|InvalidItemStateException
throws|,
name|ValueFormatException
block|{
name|PropertyState
name|p
init|=
name|getPropertyState
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
name|p
operator|+
literal|" is multi-valued."
argument_list|)
throw|;
block|}
return|return
name|p
return|;
block|}
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|ValueFormatException
throws|,
name|InvalidItemStateException
block|{
return|return
name|getSingle
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
specifier|public
name|String
name|getString
parameter_list|()
throws|throws
name|ValueFormatException
throws|,
name|InvalidItemStateException
block|{
return|return
name|getSingle
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|PropertyState
name|getMulti
parameter_list|()
throws|throws
name|InvalidItemStateException
throws|,
name|ValueFormatException
block|{
name|PropertyState
name|p
init|=
name|getPropertyState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
name|p
operator|+
literal|" is single-valued."
argument_list|)
throw|;
block|}
return|return
name|p
return|;
block|}
comment|/**      * Set the value of the property      *      * @param value      */
specifier|public
name|void
name|setValue
parameter_list|(
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|getLocation
argument_list|()
operator|.
name|set
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|getName
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|()
throw|;
block|}
block|}
comment|/**      * Set the values of the property      *      * @param values      */
specifier|public
name|void
name|setValues
parameter_list|(
name|Iterable
argument_list|<
name|Value
argument_list|>
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|getLocation
argument_list|()
operator|.
name|set
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|getName
argument_list|()
argument_list|,
name|values
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|()
throw|;
block|}
block|}
comment|/**      * Remove the property      */
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|getLocation
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

