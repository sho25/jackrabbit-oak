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
name|commons
package|;
end_package

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
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_comment
comment|/**  * Utility methods to parse a path.  *<p>  * Each method validates the input, except if the system property  * {packageName}.SKIP_VALIDATION is set, in which case only minimal validation  * takes place within this function, so when the parameter is an illegal path,  * the the result of this method is undefined.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|PathUtils
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|SNS_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(.+)\\[[1-9][0-9]*\\]$"
argument_list|)
decl_stmt|;
specifier|private
name|PathUtils
parameter_list|()
block|{
comment|// utility class
block|}
comment|/**      * Whether the path is the root path ("/").      *      * @param path the path      * @return whether this is the root      */
specifier|public
specifier|static
name|boolean
name|denotesRoot
parameter_list|(
name|String
name|path
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|path
operator|+
literal|"]"
assert|;
return|return
name|denotesRootPath
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|denotesRootPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**      * @param element The path segment to check for being the current element      * @return {@code true} if the specified element equals "."; {@code false} otherwise.      */
specifier|public
specifier|static
name|boolean
name|denotesCurrent
parameter_list|(
name|String
name|element
parameter_list|)
block|{
return|return
literal|"."
operator|.
name|equals
argument_list|(
name|element
argument_list|)
return|;
block|}
comment|/**      * @param element The path segment to check for being the parent element      * @return {@code true} if the specified element equals ".."; {@code false} otherwise.      */
specifier|public
specifier|static
name|boolean
name|denotesParent
parameter_list|(
name|String
name|element
parameter_list|)
block|{
return|return
literal|".."
operator|.
name|equals
argument_list|(
name|element
argument_list|)
return|;
block|}
comment|/**      * Whether the path is absolute (starts with a slash) or not.      *      * @param path the path      * @return true if it starts with a slash      */
specifier|public
specifier|static
name|boolean
name|isAbsolute
parameter_list|(
name|String
name|path
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|path
operator|+
literal|"]"
assert|;
return|return
name|isAbsolutePath
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isAbsolutePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|!
name|path
operator|.
name|isEmpty
argument_list|()
operator|&&
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
return|;
block|}
comment|/**      * Get the parent of a path. The parent of the root path ("/") is the root      * path.      *      * @param path the path      * @return the parent path      */
annotation|@
name|Nonnull
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
name|getAncestorPath
argument_list|(
name|path
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**      * Get the nth ancestor of a path. The 1st ancestor is the parent path,      * 2nd ancestor the grandparent path, and so on...      *<p>      * If nth<= 0, the path argument is returned as is.      *      * @param path the path      * @param nth  indicates the ancestor level for which the path should be      *             calculated.      * @return the ancestor path      */
annotation|@
name|Nonnull
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
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|path
operator|+
literal|"]"
assert|;
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
operator|||
name|denotesRootPath
argument_list|(
name|path
argument_list|)
operator|||
name|nth
operator|<=
literal|0
condition|)
block|{
return|return
name|path
return|;
block|}
name|int
name|end
init|=
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|nth
operator|--
operator|>
literal|0
condition|)
block|{
name|pos
operator|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|,
name|end
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|end
operator|=
name|pos
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|==
literal|0
condition|)
block|{
return|return
literal|"/"
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
return|return
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
return|;
block|}
comment|/**      * Get the last element of the (absolute or relative) path. The name of the      * root node ("/") and the name of the empty path ("") is the empty path.      *      * @param path the complete path      * @return the last element      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|getName
parameter_list|(
name|String
name|path
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|path
operator|+
literal|"]"
assert|;
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
operator|||
name|denotesRootPath
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|""
return|;
block|}
name|int
name|end
init|=
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|pos
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|,
name|end
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|,
name|end
operator|+
literal|1
argument_list|)
return|;
block|}
return|return
name|path
return|;
block|}
comment|/**      * Returns the given name without the possible SNS index suffix. If the      * name does not contain an SNS index, then it is returned as-is.      *      * @param name name with a possible SNS index suffix      * @return name without the SNS index suffix      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|dropIndexFromName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|SNS_PATTERN
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
return|;
block|}
return|return
name|name
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
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|path
operator|+
literal|"]"
assert|;
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|count
init|=
literal|1
decl_stmt|,
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|isAbsolutePath
argument_list|(
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|denotesRootPath
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|i
operator|++
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|i
operator|=
name|path
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|i
argument_list|)
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
name|count
return|;
block|}
name|count
operator|++
expr_stmt|;
block|}
block|}
comment|/**      * Returns an {@code Iterable} for the path elements. The root path ("/") and the      * empty path ("") have zero elements.      *      * @param path the path      * @return an Iterable for the path elements      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|elements
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|path
operator|+
literal|"]"
assert|;
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
name|isAbsolute
argument_list|(
name|path
argument_list|)
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
name|path
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|i
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|'/'
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
name|path
operator|.
name|substring
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|=
name|path
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|next
operator|=
name|path
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
block|}
return|return
literal|true
return|;
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
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
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
comment|/**      * Concatenate path elements.      *      * @param parentPath    the parent path      * @param relativePaths the relative path elements to add      * @return the concatenated path      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|concat
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
modifier|...
name|relativePaths
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
name|parentPath
argument_list|)
operator|:
literal|"Invalid parent path ["
operator|+
name|parentPath
operator|+
literal|"]"
assert|;
name|int
name|parentLen
init|=
name|parentPath
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|relativePaths
operator|.
name|length
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
name|parentLen
operator|+
name|size
operator|*
literal|5
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|parentPath
argument_list|)
expr_stmt|;
name|boolean
name|needSlash
init|=
name|parentLen
operator|>
literal|0
operator|&&
operator|!
name|denotesRootPath
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|relativePaths
control|)
block|{
assert|assert
name|isValid
argument_list|(
name|s
argument_list|)
assert|;
if|if
condition|(
name|isAbsolutePath
argument_list|(
name|s
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot append absolute path "
operator|+
name|s
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|needSlash
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|needSlash
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Concatenate path elements.      *      * @param parentPath the parent path      * @param subPath    the subPath path to add      * @return the concatenated path      */
annotation|@
name|Nonnull
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
assert|assert
name|isValid
argument_list|(
name|parentPath
argument_list|)
operator|:
literal|"Invalid parent path ["
operator|+
name|parentPath
operator|+
literal|"]"
assert|;
assert|assert
name|isValid
argument_list|(
name|subPath
argument_list|)
operator|:
literal|"Invalid sub path ["
operator|+
name|subPath
operator|+
literal|"]"
assert|;
comment|// special cases
if|if
condition|(
name|parentPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|subPath
return|;
block|}
elseif|else
if|if
condition|(
name|subPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|parentPath
return|;
block|}
elseif|else
if|if
condition|(
name|isAbsolutePath
argument_list|(
name|subPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot append absolute path "
operator|+
name|subPath
argument_list|)
throw|;
block|}
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|denotesRootPath
argument_list|(
name|parentPath
argument_list|)
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|subPath
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Check if a path is a (direct or indirect) ancestor of another path.      *      * @param ancestor the ancestor path      * @param path     the potential offspring path      * @return true if the path is an offspring of the ancestor      */
specifier|public
specifier|static
name|boolean
name|isAncestor
parameter_list|(
name|String
name|ancestor
parameter_list|,
name|String
name|path
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
name|ancestor
argument_list|)
operator|:
literal|"Invalid parent path ["
operator|+
name|ancestor
operator|+
literal|"]"
assert|;
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|ancestor
operator|+
literal|"]"
assert|;
if|if
condition|(
name|ancestor
operator|.
name|isEmpty
argument_list|()
operator|||
name|path
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
name|denotesRoot
argument_list|(
name|ancestor
argument_list|)
condition|)
block|{
if|if
condition|(
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
name|ancestor
operator|+=
literal|"/"
expr_stmt|;
block|}
return|return
name|path
operator|.
name|startsWith
argument_list|(
name|ancestor
argument_list|)
return|;
block|}
comment|/**      * Relativize a path wrt. a parent path such that      * {@code relativize(parentPath, concat(parentPath, path)) == paths}      * holds.      *      * @param parentPath parent pth      * @param path       path to relativize      * @return relativized path      */
annotation|@
name|Nonnull
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
assert|assert
name|isValid
argument_list|(
name|parentPath
argument_list|)
operator|:
literal|"Invalid parent path ["
operator|+
name|parentPath
operator|+
literal|"]"
assert|;
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|path
operator|+
literal|"]"
assert|;
if|if
condition|(
name|parentPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|""
return|;
block|}
name|String
name|prefix
init|=
name|denotesRootPath
argument_list|(
name|parentPath
argument_list|)
condition|?
name|parentPath
else|:
name|parentPath
operator|+
literal|'/'
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot relativize "
operator|+
name|path
operator|+
literal|" wrt. "
operator|+
name|parentPath
argument_list|)
throw|;
block|}
comment|/**      * Get the index of the next slash.      *      * @param path  the path      * @param index the starting index      * @return the index of the next slash (possibly the starting index), or -1      *         if not found      */
specifier|public
specifier|static
name|int
name|getNextSlash
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|index
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
name|path
argument_list|)
operator|:
literal|"Invalid path ["
operator|+
name|path
operator|+
literal|"]"
assert|;
return|return
name|path
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|index
argument_list|)
return|;
block|}
comment|/**      * Check if the path is valid, and throw an IllegalArgumentException if not.      * A valid path is absolute (starts with a '/') or relative (doesn't start      * with '/'), and contains none or more elements. A path may not end with      * '/', except for the root path. Elements itself must be at least one      * character long.      *      * @param path the path      */
specifier|public
specifier|static
name|void
name|validate
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
operator|||
name|denotesRootPath
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'/'
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Path may not end with '/': "
operator|+
name|path
argument_list|)
throw|;
block|}
name|char
name|last
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|,
name|len
init|=
name|path
operator|.
name|length
argument_list|()
init|;
name|index
operator|<
name|len
condition|;
name|index
operator|++
control|)
block|{
name|char
name|c
init|=
name|path
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'/'
condition|)
block|{
if|if
condition|(
name|last
operator|==
literal|'/'
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Path may not contains '//': "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
name|last
operator|=
name|c
expr_stmt|;
block|}
block|}
comment|/**      * Check if the path is valid. A valid path is absolute (starts with a '/')      * or relative (doesn't start with '/'), and contains none or more elements.      * A path may not end with '/', except for the root path. Elements itself must      * be at least one character long.      *      * @param path the path      * @return {@code true} iff the path is valid.      */
specifier|public
specifier|static
name|boolean
name|isValid
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
operator|||
name|denotesRootPath
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'/'
condition|)
block|{
return|return
literal|false
return|;
block|}
name|char
name|last
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|,
name|len
init|=
name|path
operator|.
name|length
argument_list|()
init|;
name|index
operator|<
name|len
condition|;
name|index
operator|++
control|)
block|{
name|char
name|c
init|=
name|path
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'/'
condition|)
block|{
if|if
condition|(
name|last
operator|==
literal|'/'
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|last
operator|=
name|c
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

