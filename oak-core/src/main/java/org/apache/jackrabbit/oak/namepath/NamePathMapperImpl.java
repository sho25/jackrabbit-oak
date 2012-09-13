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
name|namepath
package|;
end_package

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
name|plugins
operator|.
name|identifier
operator|.
name|IdentifierManager
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
comment|/**  * NamePathMapperImpl...  */
end_comment

begin_class
specifier|public
class|class
name|NamePathMapperImpl
implements|implements
name|NamePathMapper
block|{
comment|/**      * logger instance      */
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
name|NamePathMapperImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|nameMapper
decl_stmt|;
specifier|private
specifier|final
name|IdentifierManager
name|idManager
decl_stmt|;
specifier|public
name|NamePathMapperImpl
parameter_list|(
name|NameMapper
name|nameMapper
parameter_list|)
block|{
name|this
operator|.
name|nameMapper
operator|=
name|nameMapper
expr_stmt|;
name|this
operator|.
name|idManager
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|NamePathMapperImpl
parameter_list|(
name|NameMapper
name|nameMapper
parameter_list|,
name|IdentifierManager
name|idManager
parameter_list|)
block|{
name|this
operator|.
name|nameMapper
operator|=
name|nameMapper
expr_stmt|;
name|this
operator|.
name|idManager
operator|=
name|idManager
expr_stmt|;
block|}
comment|//---------------------------------------------------------< NameMapper>---
annotation|@
name|Override
specifier|public
name|String
name|getOakName
parameter_list|(
name|String
name|jcrName
parameter_list|)
block|{
return|return
name|nameMapper
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJcrName
parameter_list|(
name|String
name|oakName
parameter_list|)
block|{
return|return
name|nameMapper
operator|.
name|getJcrName
argument_list|(
name|oakName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSessionLocalMappings
parameter_list|()
block|{
return|return
name|nameMapper
operator|.
name|hasSessionLocalMappings
argument_list|()
return|;
block|}
comment|//---------------------------------------------------------< PathMapper>---
annotation|@
name|Override
specifier|public
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|getOakPath
argument_list|(
name|jcrPath
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOakPathKeepIndex
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|getOakPath
argument_list|(
name|jcrPath
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getJcrPath
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|elements
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
comment|// avoid the need to special case the root path later on
return|return
literal|"/"
return|;
block|}
name|JcrPathParser
operator|.
name|Listener
name|listener
init|=
operator|new
name|JcrPathParser
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|root
parameter_list|()
block|{
if|if
condition|(
operator|!
name|elements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"/ on non-empty path"
argument_list|)
throw|;
block|}
name|elements
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|current
parameter_list|()
block|{
comment|// nothing to do here
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|parent
parameter_list|()
block|{
if|if
condition|(
name|elements
operator|.
name|isEmpty
argument_list|()
operator|||
literal|".."
operator|.
name|equals
argument_list|(
name|elements
operator|.
name|get
argument_list|(
name|elements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|elements
operator|.
name|add
argument_list|(
literal|".."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|elements
operator|.
name|remove
argument_list|(
name|elements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|message
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|name
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"index> 1"
argument_list|)
throw|;
block|}
name|String
name|p
init|=
name|nameMapper
operator|.
name|getJcrName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|elements
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|JcrPathParser
operator|.
name|parse
argument_list|(
name|oakPath
argument_list|,
name|listener
argument_list|)
expr_stmt|;
comment|// empty path: map to "."
if|if
condition|(
name|elements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|"."
return|;
block|}
name|StringBuilder
name|jcrPath
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|elements
control|)
block|{
if|if
condition|(
name|element
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// root
name|jcrPath
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jcrPath
operator|.
name|append
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|jcrPath
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
block|}
name|jcrPath
operator|.
name|deleteCharAt
argument_list|(
name|jcrPath
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|jcrPath
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|,
specifier|final
name|boolean
name|keepIndex
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|elements
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|parseErrors
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|jcrPath
argument_list|)
condition|)
block|{
comment|// avoid the need to special case the root path later on
return|return
literal|"/"
return|;
block|}
name|int
name|length
init|=
name|jcrPath
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// identifier path?
if|if
condition|(
name|length
operator|>
literal|0
operator|&&
name|jcrPath
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'['
condition|)
block|{
if|if
condition|(
name|jcrPath
operator|.
name|charAt
argument_list|(
name|length
operator|-
literal|1
argument_list|)
operator|!=
literal|']'
condition|)
block|{
comment|// TODO error handling?
name|log
operator|.
name|debug
argument_list|(
literal|"Could not parse path "
operator|+
name|jcrPath
operator|+
literal|": unterminated identifier"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|this
operator|.
name|idManager
operator|==
literal|null
condition|)
block|{
comment|// TODO error handling?
name|log
operator|.
name|debug
argument_list|(
literal|"Could not parse path "
operator|+
name|jcrPath
operator|+
literal|": could not resolve identifier"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|this
operator|.
name|idManager
operator|.
name|getPath
argument_list|(
name|jcrPath
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|length
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
name|boolean
name|hasClarkBrackets
init|=
literal|false
decl_stmt|;
name|boolean
name|hasIndexBrackets
init|=
literal|false
decl_stmt|;
name|boolean
name|hasColon
init|=
literal|false
decl_stmt|;
name|boolean
name|hasNameStartingWithDot
init|=
literal|false
decl_stmt|;
name|char
name|prev
init|=
literal|0
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|jcrPath
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'{'
operator|||
name|c
operator|==
literal|'}'
condition|)
block|{
name|hasClarkBrackets
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'['
operator|||
name|c
operator|==
literal|']'
condition|)
block|{
name|hasIndexBrackets
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|':'
condition|)
block|{
name|hasColon
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'.'
operator|&&
operator|(
name|prev
operator|==
literal|0
operator|||
name|prev
operator|==
literal|'/'
operator|)
condition|)
block|{
name|hasNameStartingWithDot
operator|=
literal|true
expr_stmt|;
block|}
name|prev
operator|=
name|c
expr_stmt|;
block|}
comment|// try a shortcut
if|if
condition|(
operator|!
name|hasNameStartingWithDot
operator|&&
operator|!
name|hasClarkBrackets
operator|&&
operator|!
name|hasIndexBrackets
condition|)
block|{
if|if
condition|(
operator|!
name|hasColon
operator|||
operator|!
name|hasSessionLocalMappings
argument_list|()
condition|)
block|{
return|return
name|validateJcrPath
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
block|}
name|JcrPathParser
operator|.
name|Listener
name|listener
init|=
operator|new
name|JcrPathParser
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|root
parameter_list|()
block|{
if|if
condition|(
operator|!
name|elements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|parseErrors
operator|.
name|append
argument_list|(
literal|"/ on non-empty path"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|elements
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|current
parameter_list|()
block|{
comment|// nothing to do here
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|parent
parameter_list|()
block|{
if|if
condition|(
name|elements
operator|.
name|isEmpty
argument_list|()
operator|||
literal|".."
operator|.
name|equals
argument_list|(
name|elements
operator|.
name|get
argument_list|(
name|elements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|elements
operator|.
name|add
argument_list|(
literal|".."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|elements
operator|.
name|remove
argument_list|(
name|elements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|parseErrors
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|name
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
operator|!
name|keepIndex
operator|&&
name|index
operator|>
literal|1
condition|)
block|{
name|parseErrors
operator|.
name|append
argument_list|(
literal|"index> 1"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|String
name|p
init|=
name|nameMapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|parseErrors
operator|.
name|append
argument_list|(
literal|"Invalid name: "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|keepIndex
operator|&&
name|index
operator|>
literal|0
condition|)
block|{
name|p
operator|+=
literal|"["
operator|+
name|index
operator|+
literal|']'
expr_stmt|;
block|}
name|elements
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|JcrPathParser
operator|.
name|parse
argument_list|(
name|jcrPath
argument_list|,
name|listener
argument_list|)
expr_stmt|;
if|if
condition|(
name|parseErrors
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not parse path "
operator|+
name|jcrPath
operator|+
literal|": "
operator|+
name|parseErrors
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Empty path maps to ""
if|if
condition|(
name|elements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuilder
name|oakPath
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|elements
control|)
block|{
if|if
condition|(
name|element
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// root
name|oakPath
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|oakPath
operator|.
name|append
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|oakPath
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
block|}
comment|// root path is special-cased early on so it does not need to
comment|// be considered here
name|oakPath
operator|.
name|deleteCharAt
argument_list|(
name|oakPath
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|oakPath
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Validate a jcrPath assuming it doesn't contain any of the following      * characters: {@code {, }, [, ], ., :}.      * @param jcrPath  path to validate      * @return  {@code jcrPath} i.e. the same string instance if valid.      *      {@code null} otherwise.      */
specifier|private
name|String
name|validateJcrPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|parseErrors
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|JcrPathParser
operator|.
name|Listener
name|listener
init|=
operator|new
name|JcrPathParser
operator|.
name|Listener
argument_list|()
block|{
name|boolean
name|hasRoot
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|root
parameter_list|()
block|{
if|if
condition|(
name|hasRoot
condition|)
block|{
name|parseErrors
operator|.
name|append
argument_list|(
literal|"/ on non-empty path"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|hasRoot
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|current
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|parent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|parseErrors
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|name
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|String
name|p
init|=
name|nameMapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|parseErrors
operator|.
name|append
argument_list|(
literal|"Invalid name: "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
decl_stmt|;
name|JcrPathParser
operator|.
name|parse
argument_list|(
name|jcrPath
argument_list|,
name|listener
argument_list|)
expr_stmt|;
if|if
condition|(
name|parseErrors
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not parse path "
operator|+
name|jcrPath
operator|+
literal|": "
operator|+
name|parseErrors
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|jcrPath
return|;
block|}
block|}
block|}
end_class

end_unit

