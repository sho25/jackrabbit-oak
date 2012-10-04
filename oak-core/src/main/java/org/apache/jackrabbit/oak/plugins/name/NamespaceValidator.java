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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_class
class|class
name|NamespaceValidator
extends|extends
name|DefaultValidator
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
specifier|public
name|NamespaceValidator
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Validator>---
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
name|String
name|prefix
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// ignore jcr:primaryType
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"jcr:primaryType"
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NamespaceValidatorException
argument_list|(
literal|"Namespace mapping already registered"
argument_list|,
name|prefix
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|Namespaces
operator|.
name|isValidPrefix
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
if|if
condition|(
name|after
operator|.
name|isArray
argument_list|()
operator|||
operator|!
name|STRING
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NamespaceValidatorException
argument_list|(
literal|"Invalid namespace mapping"
argument_list|,
name|prefix
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NamespaceValidatorException
argument_list|(
literal|"XML prefixes are reserved"
argument_list|,
name|prefix
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|map
operator|.
name|containsValue
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
name|modificationNotAllowed
argument_list|(
name|prefix
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|NamespaceValidatorException
argument_list|(
literal|"Not a valid namespace prefix"
argument_list|,
name|prefix
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|modificationNotAllowed
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// TODO: Check whether this namespace is still used in content
block|}
block|}
specifier|private
specifier|static
name|NamespaceValidatorException
name|modificationNotAllowed
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
operator|new
name|NamespaceValidatorException
argument_list|(
literal|"Namespace modification not allowed"
argument_list|,
name|prefix
argument_list|)
return|;
block|}
block|}
end_class

end_unit

