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
name|Scalar
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
name|kernel
operator|.
name|ScalarImpl
import|;
end_import

begin_comment
comment|// FIXME: Use only the API
end_comment

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|UnsupportedRepositoryOperationException
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
name|ValueFactory
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
name|Scalar
name|toScalar
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|propertyType
parameter_list|)
throws|throws
name|RepositoryException
block|{
switch|switch
condition|(
name|propertyType
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|stringScalar
argument_list|(
name|value
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|doubleScalar
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|LONG
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|longScalar
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|booleanScalar
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|BINARY
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|binaryScalar
argument_list|(
name|value
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
case|case
name|PropertyType
operator|.
name|DATE
case|:
case|case
name|PropertyType
operator|.
name|NAME
case|:
case|case
name|PropertyType
operator|.
name|PATH
case|:
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
case|case
name|PropertyType
operator|.
name|URI
case|:
default|default:
block|{
comment|// todo implement toScalar
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"toScalar"
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
specifier|static
name|Scalar
name|toScalar
parameter_list|(
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
switch|switch
condition|(
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|stringScalar
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|doubleScalar
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|LONG
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|longScalar
argument_list|(
name|value
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
block|{
return|return
name|ScalarImpl
operator|.
name|booleanScalar
argument_list|(
name|value
operator|.
name|getBoolean
argument_list|()
argument_list|)
return|;
block|}
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
case|case
name|PropertyType
operator|.
name|BINARY
case|:
case|case
name|PropertyType
operator|.
name|DATE
case|:
case|case
name|PropertyType
operator|.
name|NAME
case|:
case|case
name|PropertyType
operator|.
name|PATH
case|:
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
case|case
name|PropertyType
operator|.
name|URI
case|:
default|default:
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"toScalar"
argument_list|)
throw|;
comment|// todo implement toScalar
block|}
block|}
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|Scalar
argument_list|>
name|toScalar
parameter_list|(
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|Scalar
argument_list|>
name|scalars
init|=
operator|new
name|ArrayList
argument_list|<
name|Scalar
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Value
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|scalars
operator|.
name|add
argument_list|(
name|toScalar
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|scalars
return|;
block|}
specifier|public
specifier|static
name|Value
name|toValue
parameter_list|(
name|ValueFactory
name|valueFactory
parameter_list|,
name|Scalar
name|scalar
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
block|{
switch|switch
condition|(
name|scalar
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|BOOLEAN
case|:
return|return
name|valueFactory
operator|.
name|createValue
argument_list|(
name|scalar
operator|.
name|getBoolean
argument_list|()
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
name|valueFactory
operator|.
name|createValue
argument_list|(
name|scalar
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
name|valueFactory
operator|.
name|createValue
argument_list|(
name|scalar
operator|.
name|getDouble
argument_list|()
argument_list|)
return|;
case|case
name|STRING
case|:
return|return
name|valueFactory
operator|.
name|createValue
argument_list|(
name|scalar
operator|.
name|getString
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"toValue"
argument_list|)
throw|;
comment|// todo implement toValue
block|}
block|}
specifier|public
specifier|static
name|Value
index|[]
name|toValues
parameter_list|(
name|ValueFactory
name|valueFactory
parameter_list|,
name|Iterable
argument_list|<
name|Scalar
argument_list|>
name|scalars
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
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
name|Scalar
name|scalar
range|:
name|scalars
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|toValue
argument_list|(
name|valueFactory
argument_list|,
name|scalar
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

