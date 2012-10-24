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
package|;
end_package

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
name|core
operator|.
name|TreeImpl
operator|.
name|PropertyLocation
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
comment|/**      * Get the value of the property      * @return  the value of the property      * @throws InvalidItemStateException      */
annotation|@
name|Nonnull
specifier|public
name|Value
name|getValue
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|getPropertyState
argument_list|()
argument_list|,
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Get the values of the property      * @return  the values of the property      * @throws InvalidItemStateException      */
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|Value
argument_list|>
name|getValues
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|getPropertyState
argument_list|()
argument_list|,
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Determine whether the property is multi valued      * @return  {@code true} if multi valued      */
specifier|public
name|boolean
name|isMultivalue
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
comment|/**      * Set the value of the property      * @param value      */
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
expr_stmt|;
block|}
comment|/**      * Set the values of the property      * @param values      */
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
expr_stmt|;
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
comment|//------------------------------------------------------------< private>---
annotation|@
name|Nonnull
specifier|private
name|PropertyState
name|getPropertyState
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getLocation
argument_list|()
operator|.
name|getProperty
argument_list|()
return|;
comment|// Not null
block|}
annotation|@
name|Override
specifier|public
name|PropertyLocation
name|getLocation
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|TreeLocation
name|location
init|=
name|super
operator|.
name|getLocation
argument_list|()
decl_stmt|;
if|if
condition|(
name|location
operator|.
name|getProperty
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Property is stale"
argument_list|)
throw|;
block|}
return|return
operator|(
name|PropertyLocation
operator|)
name|location
return|;
block|}
block|}
end_class

end_unit

