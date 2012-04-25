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
name|jcr
operator|.
name|util
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
name|CoreValue
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
name|jcr
operator|.
name|SessionContext
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
name|jcr
operator|.
name|SessionImpl
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
name|jcr
operator|.
name|value
operator|.
name|ValueFactoryImpl
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * Utility class for converting between internal value representation and JCR  * values.  * todo: needs refactoring. see OAK-16.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ValueConverter
block|{
specifier|private
name|ValueConverter
parameter_list|()
block|{}
specifier|public
specifier|static
name|CoreValue
name|toCoreValue
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|propertyType
parameter_list|,
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|)
throws|throws
name|ValueFormatException
block|{
return|return
name|toCoreValue
argument_list|(
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|,
name|propertyType
argument_list|)
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|CoreValue
name|toCoreValue
parameter_list|(
name|Value
name|value
parameter_list|,
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|)
block|{
name|ValueFactoryImpl
name|vf
init|=
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
return|return
name|vf
operator|.
name|getCoreValue
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|CoreValue
argument_list|>
name|toCoreValues
parameter_list|(
name|String
index|[]
name|values
parameter_list|,
name|int
name|propertyType
parameter_list|,
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|)
throws|throws
name|ValueFormatException
block|{
name|Value
index|[]
name|vs
init|=
operator|new
name|Value
index|[
name|values
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vs
index|[
name|i
index|]
operator|=
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|propertyType
argument_list|)
expr_stmt|;
block|}
return|return
name|toCoreValues
argument_list|(
name|vs
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|CoreValue
argument_list|>
name|toCoreValues
parameter_list|(
name|Value
index|[]
name|values
parameter_list|,
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|)
block|{
name|List
argument_list|<
name|CoreValue
argument_list|>
name|cvs
init|=
operator|new
name|ArrayList
argument_list|<
name|CoreValue
argument_list|>
argument_list|(
name|values
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|Value
name|jcrValue
range|:
name|values
control|)
block|{
if|if
condition|(
name|jcrValue
operator|!=
literal|null
condition|)
block|{
name|cvs
operator|.
name|add
argument_list|(
name|toCoreValue
argument_list|(
name|jcrValue
argument_list|,
name|sessionContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|cvs
return|;
block|}
specifier|public
specifier|static
name|Value
name|toValue
parameter_list|(
name|CoreValue
name|coreValue
parameter_list|,
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|)
block|{
return|return
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|coreValue
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Value
index|[]
name|toValues
parameter_list|(
name|Iterable
argument_list|<
name|CoreValue
argument_list|>
name|coreValues
parameter_list|,
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|)
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Value
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|coreValues
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|toValue
argument_list|(
name|cv
argument_list|,
name|sessionContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

