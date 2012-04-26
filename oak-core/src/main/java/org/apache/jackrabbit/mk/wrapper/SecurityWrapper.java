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
name|mk
operator|.
name|wrapper
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|json
operator|.
name|JsopReader
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
name|mk
operator|.
name|json
operator|.
name|JsopStream
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
name|mk
operator|.
name|json
operator|.
name|JsopTokenizer
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
name|mk
operator|.
name|json
operator|.
name|JsopWriter
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
name|mk
operator|.
name|simple
operator|.
name|NodeImpl
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
name|mk
operator|.
name|simple
operator|.
name|NodeMap
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
name|mk
operator|.
name|util
operator|.
name|ExceptionFactory
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
name|mk
operator|.
name|util
operator|.
name|PathUtils
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
name|mk
operator|.
name|util
operator|.
name|SimpleLRUCache
import|;
end_import

begin_comment
comment|/**  * A microkernel prototype implementation that filters nodes based on simple  * access rights. Each user has a password, and (optionally) a list of rights,  * stored as follows: /:user/x { password: "123", rights: "a" } Each node can  * require the user has a certain right: /data { ":right": "a" } Access rights  * are recursive. There is a special right "admin" which means everything is  * allowed, and "write" meaning a user can write.  *<p>  * This implementation is not meant for production, it is only used to find  * (performance and other) problems when using such an approach.  */
end_comment

begin_class
specifier|public
class|class
name|SecurityWrapper
extends|extends
name|MicroKernelWrapperBase
implements|implements
name|MicroKernel
block|{
specifier|private
specifier|final
name|MicroKernelWrapper
name|mk
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|admin
decl_stmt|,
name|write
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|userRights
decl_stmt|;
specifier|private
specifier|final
name|NodeMap
name|map
init|=
operator|new
name|NodeMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SimpleLRUCache
argument_list|<
name|String
argument_list|,
name|NodeImpl
argument_list|>
name|cache
init|=
name|SimpleLRUCache
operator|.
name|newInstance
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|private
name|String
name|rightsRevision
decl_stmt|;
comment|/**      * Decorates the given {@link MicroKernel} with authentication and      * authorization. The responsibility of properly disposing the given      * MikroKernel instance remains with the caller.      *      * @param mk the wrapped kernel      * @param user the user name      * @param pass the password      */
specifier|public
name|SecurityWrapper
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|pass
parameter_list|)
block|{
name|this
operator|.
name|mk
operator|=
name|MicroKernelWrapperBase
operator|.
name|wrap
argument_list|(
name|mk
argument_list|)
expr_stmt|;
comment|// TODO security for the index mechanism
name|String
name|role
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/:user/"
operator|+
name|user
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
decl_stmt|;
name|NodeMap
name|map
init|=
operator|new
name|NodeMap
argument_list|()
decl_stmt|;
name|JsopReader
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|role
argument_list|)
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|NodeImpl
name|n
init|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|map
argument_list|,
name|t
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
name|n
operator|.
name|getProperty
argument_list|(
literal|"password"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|pass
operator|.
name|equals
argument_list|(
name|password
argument_list|)
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Wrong password"
argument_list|)
throw|;
block|}
name|String
index|[]
name|rights
init|=
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
name|n
operator|.
name|getProperty
argument_list|(
literal|"rights"
argument_list|)
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|this
operator|.
name|userRights
operator|=
name|rights
expr_stmt|;
name|boolean
name|isAdmin
init|=
literal|false
decl_stmt|;
name|boolean
name|canWrite
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|r
range|:
name|rights
control|)
block|{
if|if
condition|(
name|r
operator|.
name|equals
argument_list|(
literal|"admin"
argument_list|)
condition|)
block|{
name|isAdmin
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|r
operator|.
name|equals
argument_list|(
literal|"write"
argument_list|)
condition|)
block|{
name|canWrite
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|this
operator|.
name|admin
operator|=
name|isAdmin
expr_stmt|;
name|this
operator|.
name|write
operator|=
name|canWrite
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|commitStream
parameter_list|(
name|String
name|rootPath
parameter_list|,
name|JsopReader
name|jsonDiff
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|checkRights
argument_list|(
name|rootPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|admin
condition|)
block|{
name|verifyDiff
argument_list|(
name|jsonDiff
argument_list|,
name|revisionId
argument_list|,
name|rootPath
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|mk
operator|.
name|commitStream
argument_list|(
name|rootPath
argument_list|,
name|jsonDiff
argument_list|,
name|revisionId
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|String
name|getHeadRevision
parameter_list|()
block|{
return|return
name|mk
operator|.
name|getHeadRevision
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsopReader
name|getJournalStream
parameter_list|(
name|String
name|fromRevisionId
parameter_list|,
name|String
name|toRevisionId
parameter_list|,
name|String
name|filter
parameter_list|)
block|{
name|rightsRevision
operator|=
name|getHeadRevision
argument_list|()
expr_stmt|;
name|JsopReader
name|t
init|=
name|mk
operator|.
name|getJournalStream
argument_list|(
name|fromRevisionId
argument_list|,
name|toRevisionId
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
condition|)
block|{
return|return
name|t
return|;
block|}
name|t
operator|.
name|read
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|']'
argument_list|)
condition|)
block|{
return|return
operator|new
name|JsopTokenizer
argument_list|(
literal|"[]"
argument_list|)
return|;
block|}
name|JsopStream
name|buff
init|=
operator|new
name|JsopStream
argument_list|()
decl_stmt|;
name|buff
operator|.
name|array
argument_list|()
expr_stmt|;
name|String
name|revision
init|=
name|fromRevisionId
decl_stmt|;
do|do
block|{
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|object
argument_list|()
expr_stmt|;
do|do
block|{
name|String
name|key
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|buff
operator|.
name|key
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|t
operator|.
name|read
argument_list|()
expr_stmt|;
name|String
name|value
init|=
name|t
operator|.
name|getToken
argument_list|()
decl_stmt|;
name|revision
operator|=
name|value
expr_stmt|;
name|buff
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"changes"
argument_list|)
condition|)
block|{
name|t
operator|.
name|read
argument_list|()
expr_stmt|;
name|String
name|value
init|=
name|t
operator|.
name|getToken
argument_list|()
decl_stmt|;
name|value
operator|=
name|filterDiff
argument_list|(
operator|new
name|JsopTokenizer
argument_list|(
name|value
argument_list|)
argument_list|,
name|revision
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|buff
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|raw
init|=
name|t
operator|.
name|readRawValue
argument_list|()
decl_stmt|;
comment|//System.out.println(key + ":" + raw);
name|buff
operator|.
name|encodedValue
argument_list|(
name|raw
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|buff
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|buff
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|buff
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsopReader
name|diffStream
parameter_list|(
name|String
name|fromRevisionId
parameter_list|,
name|String
name|toRevisionId
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|rightsRevision
operator|=
name|getHeadRevision
argument_list|()
expr_stmt|;
name|JsopReader
name|diff
init|=
name|mk
operator|.
name|diffStream
argument_list|(
name|fromRevisionId
argument_list|,
name|toRevisionId
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
condition|)
block|{
return|return
name|diff
return|;
block|}
return|return
name|filterDiff
argument_list|(
name|diff
argument_list|,
name|toRevisionId
argument_list|)
return|;
block|}
specifier|private
name|JsopReader
name|filterDiff
parameter_list|(
name|JsopReader
name|t
parameter_list|,
name|String
name|revisionId
parameter_list|)
block|{
name|JsopStream
name|buff
init|=
operator|new
name|JsopStream
argument_list|()
decl_stmt|;
name|verifyDiff
argument_list|(
name|t
argument_list|,
name|revisionId
argument_list|,
literal|null
argument_list|,
name|buff
argument_list|)
expr_stmt|;
return|return
name|buff
return|;
block|}
specifier|private
name|void
name|verifyDiff
parameter_list|(
name|JsopReader
name|t
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|String
name|rootPath
parameter_list|,
name|JsopWriter
name|diff
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|r
init|=
name|t
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|JsopReader
operator|.
name|END
condition|)
block|{
break|break;
block|}
name|String
name|path
decl_stmt|;
if|if
condition|(
name|rootPath
operator|==
literal|null
condition|)
block|{
name|path
operator|=
name|t
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|rootPath
argument_list|,
name|t
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|r
condition|)
block|{
case|case
literal|'+'
case|:
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|NodeImpl
name|n
init|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|map
argument_list|,
name|t
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkDiff
argument_list|(
name|path
argument_list|,
name|diff
argument_list|)
condition|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|n
operator|=
name|filterAccess
argument_list|(
name|path
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
name|diff
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|diff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|value
init|=
name|t
operator|.
name|readRawValue
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|nodeName
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkDiff
argument_list|(
name|nodeName
argument_list|,
name|diff
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkPropertyRights
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|diff
operator|.
name|encodedValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|diff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
block|}
break|break;
case|case
literal|'-'
case|:
if|if
condition|(
name|checkDiff
argument_list|(
name|path
argument_list|,
name|diff
argument_list|)
condition|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
operator|.
name|value
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|diff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
literal|'^'
case|:
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|value
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|NULL
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkDiff
argument_list|(
name|path
argument_list|,
name|diff
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkPropertyRights
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|path
argument_list|)
operator|.
name|value
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|diff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|value
operator|=
name|t
operator|.
name|readRawValue
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
name|String
name|nodeName
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkDiff
argument_list|(
name|nodeName
argument_list|,
name|diff
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkPropertyRights
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|path
argument_list|)
operator|.
name|encodedValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|diff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
block|}
break|break;
case|case
literal|'>'
case|:
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|checkDiff
argument_list|(
name|path
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|String
name|position
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|to
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|target
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|to
argument_list|)
condition|)
block|{
name|to
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|rootPath
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"last"
operator|.
name|equals
argument_list|(
name|position
argument_list|)
operator|||
literal|"first"
operator|.
name|equals
argument_list|(
name|position
argument_list|)
condition|)
block|{
name|target
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|to
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// before, after
name|target
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkDiff
argument_list|(
name|target
argument_list|,
name|diff
argument_list|)
condition|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'>'
argument_list|)
operator|.
name|key
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|diff
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|diff
operator|.
name|value
argument_list|(
name|to
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|diff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|to
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|to
argument_list|)
condition|)
block|{
name|to
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|rootPath
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkDiff
argument_list|(
name|to
argument_list|,
name|diff
argument_list|)
condition|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'>'
argument_list|)
operator|.
name|key
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|diff
operator|.
name|value
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|diff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
break|break;
default|default:
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"token: "
operator|+
operator|(
name|char
operator|)
name|t
operator|.
name|getTokenType
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|boolean
name|checkDiff
parameter_list|(
name|String
name|path
parameter_list|,
name|JsopWriter
name|target
parameter_list|)
block|{
if|if
condition|(
name|checkRights
argument_list|(
name|path
argument_list|,
name|target
operator|==
literal|null
argument_list|)
condition|)
block|{
return|return
name|target
operator|!=
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|target
operator|==
literal|null
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Access denied"
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
return|return
name|mk
operator|.
name|getLength
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsopReader
name|getNodesStream
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
block|{
return|return
name|getNodesStream
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsopReader
name|getNodesStream
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|int
name|depth
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|filter
parameter_list|)
block|{
name|rightsRevision
operator|=
name|getHeadRevision
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|checkRights
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|JsopReader
name|t
init|=
name|mk
operator|.
name|getNodesStream
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|,
name|depth
argument_list|,
name|offset
argument_list|,
name|count
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
operator|||
name|t
operator|==
literal|null
condition|)
block|{
return|return
name|t
return|;
block|}
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|NodeImpl
name|n
init|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|map
argument_list|,
name|t
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|n
operator|=
name|filterAccess
argument_list|(
name|path
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|JsopStream
name|buff
init|=
operator|new
name|JsopStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// TODO childNodeCount properties might be wrong
comment|// when count and offset are used
name|n
operator|.
name|append
argument_list|(
name|buff
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsopReader
name|getRevisionsStream
parameter_list|(
name|long
name|since
parameter_list|,
name|int
name|maxEntries
parameter_list|)
block|{
return|return
name|mk
operator|.
name|getRevisionsStream
argument_list|(
name|since
argument_list|,
name|maxEntries
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nodeExists
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
block|{
name|rightsRevision
operator|=
name|getHeadRevision
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|checkRights
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|mk
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
block|{
name|rightsRevision
operator|=
name|getHeadRevision
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|checkRights
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Node not found: "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
name|mk
operator|.
name|getChildNodeCount
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|mk
operator|.
name|read
argument_list|(
name|blobId
argument_list|,
name|pos
argument_list|,
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|waitForCommit
parameter_list|(
name|String
name|oldHeadRevisionId
parameter_list|,
name|long
name|maxWaitMillis
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|mk
operator|.
name|waitForCommit
argument_list|(
name|oldHeadRevisionId
argument_list|,
name|maxWaitMillis
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|write
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|rightsRevision
operator|=
name|getHeadRevision
argument_list|()
expr_stmt|;
name|checkRights
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|mk
operator|.
name|write
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|branch
parameter_list|(
name|String
name|trunkRevisionId
parameter_list|)
block|{
comment|// TODO OAK-45 support
return|return
name|mk
operator|.
name|branch
argument_list|(
name|trunkRevisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|merge
parameter_list|(
name|String
name|branchRevisionId
parameter_list|,
name|String
name|message
parameter_list|)
block|{
comment|// TODO OAK-45 support
return|return
name|mk
operator|.
name|merge
argument_list|(
name|branchRevisionId
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|private
name|NodeImpl
name|filterAccess
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeImpl
name|n
parameter_list|)
block|{
if|if
condition|(
operator|!
name|checkRights
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|admin
operator|&&
name|n
operator|.
name|hasProperty
argument_list|(
literal|":rights"
argument_list|)
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|cloneAndSetProperty
argument_list|(
literal|":rights"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|long
name|pos
init|=
literal|0
init|;
condition|;
name|pos
operator|++
control|)
block|{
name|String
name|childName
init|=
name|n
operator|.
name|getChildNodeName
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|childName
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|NodeImpl
name|c
init|=
name|n
operator|.
name|getNode
argument_list|(
name|childName
argument_list|)
decl_stmt|;
name|NodeImpl
name|c2
init|=
name|filterAccess
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
argument_list|,
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|c2
operator|!=
name|c
condition|)
block|{
if|if
condition|(
name|c2
operator|==
literal|null
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|cloneAndRemoveChildNode
argument_list|(
name|childName
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|n
operator|=
name|n
operator|.
name|setChild
argument_list|(
name|childName
argument_list|,
name|c2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|n
return|;
block|}
specifier|private
name|boolean
name|checkPropertyRights
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|!
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
operator|.
name|equals
argument_list|(
literal|":rights"
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|checkRights
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|write
parameter_list|)
block|{
if|if
condition|(
name|admin
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|write
operator|&&
operator|!
name|this
operator|.
name|write
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|boolean
name|access
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|key
init|=
name|path
operator|+
literal|"@"
operator|+
name|rightsRevision
decl_stmt|;
name|NodeImpl
name|n
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|mk
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|,
name|rightsRevision
argument_list|)
condition|)
block|{
name|String
name|json
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|path
argument_list|,
name|rightsRevision
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JsopReader
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|n
operator|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|map
argument_list|,
name|t
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|n
operator|=
operator|new
name|NodeImpl
argument_list|(
name|map
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|Boolean
name|b
init|=
name|hasRights
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|b
condition|)
block|{
name|access
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// check parent
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
break|break;
block|}
name|path
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|access
return|;
block|}
specifier|private
name|Boolean
name|hasRights
parameter_list|(
name|NodeImpl
name|n
parameter_list|)
block|{
name|String
name|rights
init|=
name|n
operator|.
name|getProperty
argument_list|(
literal|":rights"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rights
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|rights
operator|=
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
name|rights
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|r
range|:
name|rights
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|boolean
name|got
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|u
range|:
name|userRights
control|)
block|{
if|if
condition|(
name|u
operator|.
name|equals
argument_list|(
name|r
argument_list|)
condition|)
block|{
name|got
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|got
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

