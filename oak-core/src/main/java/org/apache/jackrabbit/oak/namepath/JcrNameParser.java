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
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
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
name|util
operator|.
name|XMLChar
import|;
end_import

begin_comment
comment|/**  * Parses and validates JCR names. Upon successful completion of  * {@link #parse(String, Listener, int)}  * the specified listener is informed about the (resulting) JCR name.  * In case of failure {@link JcrNameParser.Listener#error(String)} is called indicating  * the reason.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|JcrNameParser
block|{
comment|// constants for parser
specifier|private
specifier|static
specifier|final
name|int
name|STATE_PREFIX_START
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_PREFIX
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_NAME_START
init|=
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_NAME
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_URI_START
init|=
literal|4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_URI
init|=
literal|5
decl_stmt|;
comment|/**      * Listener interface for this name parser.      */
interface|interface
name|Listener
block|{
comment|/**          * Informs this listener that parsing the jcr name failed.          *          * @param message Details about the error.          * @see JcrNameParser#parse(String, Listener, int)          */
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
comment|/**          * Informs this listener about the result of          * {@link JcrNameParser#parse(String, Listener, int)}          *          * @param name The resulting name upon successful completion of          * {@link org.apache.jackrabbit.oak.namepath.JcrNameParser#parse(String, Listener, int)}          * @param index the index (or {@code 0} when not specified)          */
name|boolean
name|name
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|index
parameter_list|)
function_decl|;
block|}
comment|/**      * Avoid instantiation      */
specifier|private
name|JcrNameParser
parameter_list|()
block|{     }
comment|/**      * Parse the specified jcr name and inform the specified {@code listener}      * about the result or any error that may occur during parsing.      *      * @param jcrName The jcr name to be parsed.      * @param listener The listener to be informed about success or failure.      * @param index index, or {@code 0} when not specified      * @return whether parsing was successful      */
specifier|public
specifier|static
name|boolean
name|parse
parameter_list|(
name|String
name|jcrName
parameter_list|,
name|Listener
name|listener
parameter_list|,
name|int
name|index
parameter_list|)
block|{
comment|// trivial check
name|int
name|len
init|=
name|jcrName
operator|==
literal|null
condition|?
literal|0
else|:
name|jcrName
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"Empty name"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
literal|"."
operator|.
name|equals
argument_list|(
name|jcrName
argument_list|)
operator|||
literal|".."
operator|.
name|equals
argument_list|(
name|jcrName
argument_list|)
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"Illegal name:"
operator|+
name|jcrName
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// parse the name
name|String
name|prefix
decl_stmt|;
name|int
name|nameStart
init|=
literal|0
decl_stmt|;
name|int
name|state
init|=
name|STATE_PREFIX_START
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|jcrName
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
literal|':'
condition|)
block|{
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"Prefix must not be empty"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX
condition|)
block|{
name|prefix
operator|=
name|jcrName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"Invalid name prefix: "
operator|+
name|prefix
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|state
operator|=
name|STATE_NAME_START
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_URI
condition|)
block|{
comment|// ignore -> validation of uri later on.
block|}
else|else
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"'"
operator|+
name|c
operator|+
literal|"' not allowed in name"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
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
operator|||
name|c
operator|==
literal|'*'
operator|||
name|c
operator|==
literal|'|'
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"'"
operator|+
name|c
operator|+
literal|"' not allowed in name"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'/'
condition|)
block|{
if|if
condition|(
name|state
operator|==
name|STATE_URI_START
condition|)
block|{
name|state
operator|=
name|STATE_URI
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|!=
name|STATE_URI
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"'"
operator|+
name|c
operator|+
literal|"' not allowed in name"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'{'
condition|)
block|{
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
condition|)
block|{
name|state
operator|=
name|STATE_URI_START
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_URI_START
operator|||
name|state
operator|==
name|STATE_URI
condition|)
block|{
comment|// second '{' in the uri-part -> no valid expanded jcr-name.
comment|// therefore reset the nameStart and change state.
name|state
operator|=
name|STATE_NAME
expr_stmt|;
name|nameStart
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_NAME_START
condition|)
block|{
name|state
operator|=
name|STATE_NAME
expr_stmt|;
name|nameStart
operator|=
name|i
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'}'
condition|)
block|{
if|if
condition|(
name|state
operator|==
name|STATE_URI_START
operator|||
name|state
operator|==
name|STATE_URI
condition|)
block|{
name|String
name|tmp
init|=
name|jcrName
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmp
operator|.
name|isEmpty
argument_list|()
operator|||
name|tmp
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// The leading "{...}" part is empty or contains
comment|// a colon, so we treat it as a valid namespace URI.
comment|// More detailed validity checks (is it well formed,
comment|// registered, etc.) are not needed here.
name|state
operator|=
name|STATE_NAME_START
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tmp
operator|.
name|equals
argument_list|(
literal|"internal"
argument_list|)
condition|)
block|{
comment|// As a special Jackrabbit backwards compatibility
comment|// feature, support {internal} as a valid URI prefix
name|state
operator|=
name|STATE_NAME_START
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tmp
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
comment|// The leading "{...}" contains neither a colon nor
comment|// a slash, so we can interpret it as a a part of a
comment|// normal local name.
name|state
operator|=
name|STATE_NAME
expr_stmt|;
name|nameStart
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"The URI prefix of the name "
operator|+
name|jcrName
operator|+
literal|" is "
operator|+
literal|"neither a valid URI nor a valid part of a local name."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
condition|)
block|{
name|state
operator|=
name|STATE_PREFIX
expr_stmt|;
comment|// prefix start -> validation later on will fail.
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_NAME_START
condition|)
block|{
name|state
operator|=
name|STATE_NAME
expr_stmt|;
name|nameStart
operator|=
name|i
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
condition|)
block|{
name|state
operator|=
name|STATE_PREFIX
expr_stmt|;
comment|// prefix start
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_NAME_START
condition|)
block|{
name|state
operator|=
name|STATE_NAME
expr_stmt|;
name|nameStart
operator|=
name|i
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_URI_START
condition|)
block|{
name|state
operator|=
name|STATE_URI
expr_stmt|;
block|}
block|}
block|}
comment|// take care of qualified jcrNames starting with '{' that are not having
comment|// a terminating '}' -> make sure there are no illegal characters present.
if|if
condition|(
name|state
operator|==
name|STATE_URI
operator|&&
operator|(
name|jcrName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|>
operator|-
literal|1
operator|||
name|jcrName
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>
operator|-
literal|1
operator|)
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"Local name may not contain ':' nor '/'"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|nameStart
operator|==
name|len
operator|||
name|state
operator|==
name|STATE_NAME_START
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|"Local name must not be empty"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|listener
operator|.
name|name
argument_list|(
name|jcrName
argument_list|,
name|index
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|validate
parameter_list|(
name|String
name|jcrName
parameter_list|)
block|{
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
block|{             }
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
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
return|return
name|parse
argument_list|(
name|jcrName
argument_list|,
name|listener
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|checkName
parameter_list|(
name|String
name|jcrName
parameter_list|,
name|boolean
name|allowResidual
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
if|if
condition|(
name|jcrName
operator|==
literal|null
operator|||
operator|!
operator|(
name|allowResidual
operator|&&
literal|"*"
operator|.
name|equals
argument_list|(
name|jcrName
argument_list|)
operator|||
name|validate
argument_list|(
name|jcrName
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Not a valid JCR name '"
operator|+
name|jcrName
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

