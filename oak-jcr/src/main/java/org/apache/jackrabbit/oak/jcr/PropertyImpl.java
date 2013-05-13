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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|UNDEFINED
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
name|AccessDeniedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Binary
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemVisitor
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
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
operator|.
name|Status
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
name|delegate
operator|.
name|NodeDelegate
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
name|delegate
operator|.
name|PropertyDelegate
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|value
operator|.
name|ValueHelper
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

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|PropertyImpl
extends|extends
name|ItemImpl
argument_list|<
name|PropertyDelegate
argument_list|>
implements|implements
name|Property
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PropertyImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Value
index|[]
name|NO_VALUES
init|=
operator|new
name|Value
index|[
literal|0
index|]
decl_stmt|;
name|PropertyImpl
parameter_list|(
name|PropertyDelegate
name|dlg
parameter_list|,
name|SessionContext
name|sessionContext
parameter_list|)
block|{
name|super
argument_list|(
name|dlg
argument_list|,
name|sessionContext
argument_list|)
expr_stmt|;
block|}
comment|//---------------------------------------------------------------< Item>---
annotation|@
name|Override
specifier|public
name|boolean
name|isNode
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Node
name|getParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|NodeImpl
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeImpl
argument_list|<
name|?
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|NodeDelegate
name|parent
init|=
name|dlg
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessDeniedException
argument_list|()
throw|;
block|}
else|else
block|{
return|return
operator|new
name|NodeImpl
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|(
name|dlg
operator|.
name|getParent
argument_list|()
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
block|}
block|}
block|)
function|;
block|}
end_class

begin_function
annotation|@
name|Override
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|safePerform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|NEW
return|;
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
return|return
name|safePerform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|MODIFIED
return|;
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|perform
argument_list|(
operator|new
name|ItemWriteOperation
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dlg
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|ItemVisitor
name|visitor
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkAlive
argument_list|()
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|//-----------------------------------------------------------< Property>---
end_comment

begin_function
annotation|@
name|Override
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
name|value
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalSetValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
specifier|final
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalSetValues
argument_list|(
name|values
argument_list|,
name|ValueHelper
operator|.
name|getType
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|String
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Value
index|[]
name|vs
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|values
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|,
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
name|internalSetValues
argument_list|(
name|vs
argument_list|,
name|UNDEFINED
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|InputStream
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Binary
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|long
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|double
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Calendar
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|boolean
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Node
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|internalRemove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalSetValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Value
name|getValue
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Value
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Value
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|dlg
operator|.
name|getSingleState
argument_list|()
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Value
index|[]
name|getValues
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|List
argument_list|<
name|Value
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Value
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|dlg
operator|.
name|getMultiState
argument_list|()
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|toArray
argument_list|(
name|NO_VALUES
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getString
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getStream
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Binary
name|getBinary
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getBinary
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getLong
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getDouble
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|BigDecimal
name|getDecimal
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getDecimal
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Calendar
name|getDate
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getDate
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getBoolean
argument_list|()
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Node
name|getNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Node
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Node
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Value
name|value
init|=
name|getValue
argument_list|()
decl_stmt|;
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
name|REFERENCE
case|:
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
name|getSession
argument_list|()
operator|.
name|getNodeByIdentifier
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
case|case
name|PropertyType
operator|.
name|NAME
case|:
name|String
name|path
init|=
name|value
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
operator|&&
name|path
operator|.
name|endsWith
argument_list|(
literal|"]"
argument_list|)
condition|)
block|{
comment|// identifier path
name|String
name|identifier
init|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|getSession
argument_list|()
operator|.
name|getNodeByIdentifier
argument_list|(
name|identifier
argument_list|)
return|;
block|}
else|else
block|{
try|try
block|{
return|return
operator|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
operator|)
condition|?
name|getSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
else|:
name|getParent
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
block|}
case|case
name|PropertyType
operator|.
name|STRING
case|:
try|try
block|{
name|Value
name|refValue
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|value
argument_list|,
name|PropertyType
operator|.
name|REFERENCE
argument_list|,
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getSession
argument_list|()
operator|.
name|getNodeByIdentifier
argument_list|(
name|refValue
operator|.
name|getString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ItemNotFoundException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// try if STRING value can be interpreted as PATH value
name|Value
name|pathValue
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|value
argument_list|,
name|PropertyType
operator|.
name|PATH
argument_list|,
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
name|path
operator|=
name|pathValue
operator|.
name|getString
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
operator|)
condition|?
name|getSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
else|:
name|getParent
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|pathValue
operator|.
name|getString
argument_list|()
argument_list|)
throw|;
block|}
block|}
default|default:
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Property value cannot be converted to a PATH, REFERENCE or WEAKREFERENCE"
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Property
name|getProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Property
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Property
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Value
name|value
init|=
name|getValue
argument_list|()
decl_stmt|;
name|Value
name|pathValue
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|value
argument_list|,
name|PropertyType
operator|.
name|PATH
argument_list|,
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|pathValue
operator|.
name|getString
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
operator|)
condition|?
name|getSession
argument_list|()
operator|.
name|getProperty
argument_list|(
name|path
argument_list|)
else|:
name|getParent
argument_list|()
operator|.
name|getProperty
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getLength
argument_list|(
name|getValue
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|long
index|[]
name|getLengths
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Value
index|[]
name|values
init|=
name|getValues
argument_list|()
decl_stmt|;
name|long
index|[]
name|lengths
init|=
operator|new
name|long
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
name|lengths
index|[
name|i
index|]
operator|=
name|getLength
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|lengths
return|;
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|PropertyDefinition
name|getDefinition
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|PropertyDefinition
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|PropertyDefinition
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|internalGetDefinition
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|isMultiple
argument_list|()
condition|)
block|{
name|Value
index|[]
name|values
init|=
name|getValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// retrieve the type from the property definition
comment|// do not require exact match (see OAK-815)
name|PropertyDefinition
name|definition
init|=
name|getDefinitionProvider
argument_list|()
operator|.
name|getDefinition
argument_list|(
name|dlg
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
argument_list|,
name|dlg
operator|.
name|getPropertyState
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|getRequiredType
argument_list|()
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
return|return
name|PropertyType
operator|.
name|STRING
return|;
block|}
else|else
block|{
return|return
name|definition
operator|.
name|getRequiredType
argument_list|()
return|;
block|}
block|}
else|else
block|{
return|return
name|values
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
else|else
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|boolean
name|isMultiple
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|isArray
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_comment
comment|//------------------------------------------------------------< internal>---
end_comment

begin_function
annotation|@
name|Override
specifier|protected
specifier|final
name|PropertyDefinition
name|internalGetDefinition
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getDefinitionProvider
argument_list|()
operator|.
name|getDefinition
argument_list|(
name|dlg
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
argument_list|,
name|dlg
operator|.
name|getPropertyState
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * Return the length of the specified JCR value object.      *      * @param value The value.      * @return The length of the given value.      * @throws RepositoryException If an error occurs.      */
end_comment

begin_function
specifier|private
specifier|static
name|long
name|getLength
parameter_list|(
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
return|return
name|value
operator|.
name|getBinary
argument_list|()
operator|.
name|getSize
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|value
operator|.
name|getString
argument_list|()
operator|.
name|length
argument_list|()
return|;
block|}
block|}
end_function

begin_function
specifier|private
name|void
name|internalRemove
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|perform
argument_list|(
operator|new
name|ItemWriteOperation
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dlg
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|void
name|internalSetValue
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|perform
argument_list|(
operator|new
name|ItemWriteOperation
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// TODO: Avoid extra JCR method calls (OAK-672)
name|PropertyDefinition
name|definition
init|=
name|internalGetDefinition
argument_list|()
decl_stmt|;
name|PropertyState
name|state
init|=
name|createSingleState
argument_list|(
name|dlg
operator|.
name|getName
argument_list|()
argument_list|,
name|value
argument_list|,
name|definition
argument_list|)
decl_stmt|;
name|dlg
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|void
name|internalSetValues
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Value
index|[]
name|values
parameter_list|,
specifier|final
name|int
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|perform
argument_list|(
operator|new
name|ItemWriteOperation
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// TODO: Avoid extra JCR method calls (OAK-672)
name|PropertyDefinition
name|definition
init|=
name|internalGetDefinition
argument_list|()
decl_stmt|;
name|PropertyState
name|state
init|=
name|createMultiState
argument_list|(
name|dlg
operator|.
name|getName
argument_list|()
argument_list|,
name|type
argument_list|,
name|values
argument_list|,
name|definition
argument_list|)
decl_stmt|;
name|dlg
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

