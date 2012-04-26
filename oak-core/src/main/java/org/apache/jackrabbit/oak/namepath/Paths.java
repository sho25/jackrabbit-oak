begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|oak
operator|.
name|namepath
operator|.
name|JcrNameParser
operator|.
name|Listener
import|;
end_import

begin_comment
comment|/**  * All path in the Oak API have the following form  *<p>  *<pre>  * PATH    := ("/" ELEMENT)* | ELEMENT ("/" ELEMENT)*  * ELEMENT := [PREFIX ":"] NAME  * PREFIX  := non empty string not containing ":" and "/"  * NAME    := non empty string not containing ":" and "/" TODO: check whether this is correct  *</pre>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Paths
block|{
specifier|private
name|Paths
parameter_list|()
block|{}
comment|/**      * Get the parent of a path. The parent of the root path ("/") is the root      * path.      *      * @param path the path      * @return the parent path      */
specifier|public
specifier|static
name|String
name|getParentPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**      * Get the last element of the (absolute or relative) path. The name of the      * root node ("/") and the name of the empty path ("") is the empty path.      *      * @param path the complete path      * @return the last element      */
specifier|public
specifier|static
name|String
name|getName
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**      * Get the nth ancestor of a path. The 1st ancestor is the parent path,      * 2nd ancestor the grandparent path, and so on...      *<p/>      * If nth<= 0, the path argument is returned as is.      *      * @param path the path      * @param nth Integer indicating which ancestor path to retrieve.      * @return the ancestor path      */
specifier|public
specifier|static
name|String
name|getAncestorPath
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|nth
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|path
argument_list|,
name|nth
argument_list|)
return|;
block|}
comment|/**      * Concatenate path elements.      *      * @param parentPath the parent path      * @param subPath the subPath path to add      * @return the concatenated path      */
specifier|public
specifier|static
name|String
name|concat
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|subPath
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|subPath
argument_list|)
return|;
block|}
comment|/**      * Relativize a path wrt. a parent path such that      * {@code relativize(parentPath, concat(parentPath, path)) == paths}      * holds.      *      * @param parentPath parent pth      * @param path path to relativize      * @return relativized path      */
specifier|public
specifier|static
name|String
name|relativize
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|relativize
argument_list|(
name|parentPath
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/**      * Calculate the number of elements in the path. The root path has zero      * elements.      *      * @param path the path      * @return the number of elements      */
specifier|public
specifier|static
name|int
name|getDepth
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**      * Get the prefix of an element. Undefined if {@code element} is      * not an {@code ELEMENT}.      * @param element      * @return  the {@code PREFIX} of {@code element} or {@code null} if none      */
specifier|public
specifier|static
name|String
name|getPrefixFromElement
parameter_list|(
name|String
name|element
parameter_list|)
block|{
name|int
name|pos
init|=
name|element
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|element
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
comment|/**      * Get the name of an element. Undefined if {@code element} is      * not an {@code ELEMENT}.      * @param element      * @return  the {@code NAME} of {@code element}      */
specifier|public
specifier|static
name|String
name|getNameFromElement
parameter_list|(
name|String
name|element
parameter_list|)
block|{
name|int
name|pos
init|=
name|element
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|element
return|;
block|}
else|else
block|{
return|return
name|element
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
comment|/**      * Determine whether {@code string} is a valid {@code ELEMENT}.      * @param string      * @return  {@code true} iff {@code string} is a valid {@code ELEMENT}.      */
specifier|public
specifier|static
name|boolean
name|isValidElement
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|string
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|colons
init|=
literal|0
decl_stmt|;
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|string
operator|.
name|length
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|string
operator|.
name|charAt
argument_list|(
name|k
argument_list|)
operator|==
literal|':'
condition|)
block|{
name|colons
operator|+=
literal|1
expr_stmt|;
name|pos
operator|=
name|k
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|string
operator|.
name|charAt
argument_list|(
name|k
argument_list|)
operator|==
literal|'/'
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|colons
operator|<=
literal|1
operator|&&
operator|(
name|pos
operator|!=
literal|0
operator|&&
name|pos
operator|!=
name|string
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|)
return|;
block|}
comment|/**      * Determine whether {@code string} is a valid {@code PATH}.      * @param string      * @return  {@code true} iff {@code string} is a valid {@code PATH}.      */
specifier|public
specifier|static
name|boolean
name|isValidPath
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|string
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|string
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|part
range|:
name|split
argument_list|(
name|string
argument_list|,
literal|'/'
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|isValidElement
argument_list|(
name|part
argument_list|)
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
specifier|public
specifier|static
name|String
name|toOakName
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|NameMapper
name|mapper
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|element
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Listener
name|listener
init|=
operator|new
name|JcrNameParser
operator|.
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
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|message
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|p
init|=
name|mapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|element
operator|.
name|append
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|JcrNameParser
operator|.
name|parse
argument_list|(
name|name
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
name|element
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|toOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|,
specifier|final
name|NameMapper
name|mapper
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
name|jcrPath
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
name|void
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
name|RuntimeException
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|identifier
parameter_list|(
name|String
name|identifier
parameter_list|)
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
name|RuntimeException
argument_list|(
literal|"[identifier] on non-empty path"
argument_list|)
throw|;
block|}
name|elements
operator|.
name|add
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
comment|// todo resolve identifier
comment|// todo seal
block|}
annotation|@
name|Override
specifier|public
name|void
name|current
parameter_list|()
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
specifier|public
name|void
name|parent
parameter_list|()
block|{
if|if
condition|(
name|elements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|".. of empty path"
argument_list|)
throw|;
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|(
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
name|RuntimeException
argument_list|(
literal|"index> 1"
argument_list|)
throw|;
block|}
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
name|RuntimeException
argument_list|(
name|message
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|p
init|=
name|mapper
operator|.
name|getOakName
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
specifier|public
specifier|static
name|String
name|toJcrPath
parameter_list|(
name|String
name|oakPath
parameter_list|,
specifier|final
name|NameMapper
name|mapper
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
name|void
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
name|RuntimeException
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|identifier
parameter_list|(
name|String
name|identifier
parameter_list|)
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
name|RuntimeException
argument_list|(
literal|"[identifier] on non-empty path"
argument_list|)
throw|;
block|}
name|elements
operator|.
name|add
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
comment|// todo resolve identifier
comment|// todo seal
block|}
annotation|@
name|Override
specifier|public
name|void
name|current
parameter_list|()
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
specifier|public
name|void
name|parent
parameter_list|()
block|{
if|if
condition|(
name|elements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|".. of empty path"
argument_list|)
throw|;
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|(
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
name|RuntimeException
argument_list|(
literal|"index> 1"
argument_list|)
throw|;
block|}
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
name|RuntimeException
argument_list|(
name|message
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|p
init|=
name|mapper
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
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|split
parameter_list|(
specifier|final
name|String
name|string
parameter_list|,
specifier|final
name|char
name|separator
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
name|int
name|pos
init|=
operator|!
name|string
operator|.
name|isEmpty
argument_list|()
operator|&&
name|string
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|separator
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|String
name|next
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|pos
operator|>=
name|string
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|int
name|i
init|=
name|string
operator|.
name|indexOf
argument_list|(
name|separator
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|next
operator|=
name|string
operator|.
name|substring
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|=
name|string
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|next
operator|=
name|string
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|pos
operator|=
name|i
operator|+
literal|1
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|next
init|=
name|this
operator|.
name|next
decl_stmt|;
name|this
operator|.
name|next
operator|=
literal|null
expr_stmt|;
return|return
name|next
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

