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
name|plugins
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|cache
operator|.
name|CacheValue
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
name|commons
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
name|commons
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

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
name|checkArgument
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|elementsEqual
import|;
end_import

begin_comment
comment|/**  * The {@code Path} class is closely modeled after the semantics of  * {@code PathUtils} in oak-commons. Corresponding methods in this class can  * be used as a replacement for the methods in {@code PathUtils} on {@code Path}  * objects.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Path
implements|implements
name|CacheValue
implements|,
name|Comparable
argument_list|<
name|Path
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|Path
name|ROOT
init|=
operator|new
name|Path
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
literal|""
operator|.
name|hashCode
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Nullable
specifier|private
specifier|final
name|Path
name|parent
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|int
name|hash
decl_stmt|;
specifier|private
name|Path
parameter_list|(
annotation|@
name|Nullable
name|Path
name|parent
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
name|int
name|hash
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|hash
operator|=
name|hash
expr_stmt|;
block|}
comment|/**      * Creates a new {@code Path} from the given parent {@code Path}. The name      * of the new {@code Path} cannot be the empty {@code String}.      *      * @param parent the parent {@code Path}.      * @param name the name of the new {@code Path}.      * @throws IllegalArgumentException if the {@code name} is empty.      */
specifier|public
name|Path
parameter_list|(
annotation|@
name|NotNull
name|Path
name|parent
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|checkNotNull
argument_list|(
name|parent
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"name cannot be the empty String"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a relative path with a single name element. The name cannot be      * the empty {@code String}.      *      * @param name the name of the first path element.      * @throws IllegalArgumentException if the {@code name} is empty.      */
specifier|public
name|Path
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"name cannot be the empty String"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|==
operator|-
literal|1
argument_list|,
literal|"name must not contain path separator: {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the name of this path. The {@link #ROOT} is the only path with      * an empty name. That is a String with length zero.      *      * @return the name of this path.      */
annotation|@
name|NotNull
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Returns the names of the path elements with increasing {@link #getDepth()}      * starting at depth 1.      *       * @return the names of the path elements.      */
annotation|@
name|NotNull
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|elements
parameter_list|()
block|{
return|return
name|elements
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**      * Returns {@code true} if this is the {@link #ROOT} path; {@code false}      * otherwise.      *      * @return whether this is the {@link #ROOT} path.      */
specifier|public
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|name
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * The parent of this path or {@code null} if this path does not have a      * parent. The {@link #ROOT} path and the first path element of a relative      * path do not have a parent.      *      * @return the parent of this path or {@code null} if this path does not      *      have a parent.      */
annotation|@
name|Nullable
specifier|public
name|Path
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**      * @return the number of characters of the {@code String} representation of      *  this path.      */
specifier|public
name|int
name|length
parameter_list|()
block|{
if|if
condition|(
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
name|int
name|length
init|=
literal|0
decl_stmt|;
name|Path
name|p
init|=
name|this
decl_stmt|;
while|while
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|length
operator|+=
name|p
operator|.
name|name
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|length
operator|++
expr_stmt|;
block|}
name|p
operator|=
name|p
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|length
return|;
block|}
comment|/**      * The depth of this path. The {@link #ROOT} has a depth of 0. The path      * {@code /foo/bar} as well as {@code bar/baz} have depth 2.      *      * @return the depth of the path.      */
specifier|public
name|int
name|getDepth
parameter_list|()
block|{
return|return
name|getNumberOfPathElements
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**      * Get the nth ancestor of a path. The 1st ancestor is the parent path,      * 2nd ancestor the grandparent path, and so on...      *<p>      * If {@code nth<= 0}, then this path is returned.      *      * @param nth  indicates the ancestor level for which the path should be      *             calculated.      * @return the ancestor path      */
annotation|@
name|NotNull
specifier|public
name|Path
name|getAncestor
parameter_list|(
name|int
name|nth
parameter_list|)
block|{
name|Path
name|p
init|=
name|this
decl_stmt|;
while|while
condition|(
name|nth
operator|--
operator|>
literal|0
operator|&&
name|p
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|p
operator|=
name|p
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
comment|/**      * Return {@code true} if {@code this} path is an ancestor of the      * {@code other} path, otherwise {@code false}.      *      * @param other the other path.      * @return whether this path is an ancestor of the other path.      */
specifier|public
name|boolean
name|isAncestorOf
parameter_list|(
annotation|@
name|NotNull
name|Path
name|other
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|int
name|depthDiff
init|=
name|other
operator|.
name|getDepth
argument_list|()
operator|-
name|getDepth
argument_list|()
decl_stmt|;
return|return
name|depthDiff
operator|>
literal|0
operator|&&
name|elementsEqual
argument_list|(
name|elements
argument_list|(
literal|true
argument_list|)
argument_list|,
name|other
operator|.
name|getAncestor
argument_list|(
name|depthDiff
argument_list|)
operator|.
name|elements
argument_list|(
literal|true
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @return {@code true} if this is an absolute path; {@code false} otherwise.      */
specifier|public
name|boolean
name|isAbsolute
parameter_list|()
block|{
name|Path
name|p
init|=
name|this
decl_stmt|;
while|while
condition|(
name|p
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|p
operator|=
name|p
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|p
operator|.
name|isRoot
argument_list|()
return|;
block|}
comment|/**      * Creates a {@code Path} from a {@code String}.      *      * @param path the {@code String} to parse.      * @return the {@code Path} from the {@code String}.      * @throws IllegalArgumentException if the {@code path} is the empty      *      {@code String}.      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|Path
name|fromString
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|p
operator|=
name|ROOT
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|name
operator|=
name|StringCache
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|p
operator|=
operator|new
name|Path
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|p
operator|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
name|StringCache
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path must not be empty"
argument_list|)
throw|;
block|}
return|return
name|p
return|;
block|}
comment|/**      * Appends the {@code String} representation of this {@code Path} to the      * passed {@code StringBuilder}. See also {@link #toString()}.      *      * @param sb the {@code StringBuilder} this {@code Path} is appended to.      * @return the passed {@code StringBuilder}.      */
annotation|@
name|NotNull
specifier|public
name|StringBuilder
name|toStringBuilder
parameter_list|(
annotation|@
name|NotNull
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|isRoot
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
name|int
name|memory
init|=
literal|0
decl_stmt|;
name|Path
name|p
init|=
name|this
decl_stmt|;
while|while
condition|(
name|p
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|memory
operator|+=
literal|24
expr_stmt|;
comment|// shallow size
name|memory
operator|+=
name|StringUtils
operator|.
name|estimateMemoryUsage
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|p
operator|=
name|p
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|memory
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
annotation|@
name|NotNull
name|Path
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|0
return|;
block|}
name|Path
name|t
init|=
name|this
decl_stmt|;
name|int
name|off
init|=
name|t
operator|.
name|getNumberOfPathElements
argument_list|(
literal|true
argument_list|)
operator|-
name|checkNotNull
argument_list|(
name|other
argument_list|)
operator|.
name|getNumberOfPathElements
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|int
name|corrected
init|=
name|off
decl_stmt|;
while|while
condition|(
name|corrected
operator|>
literal|0
condition|)
block|{
name|t
operator|=
name|t
operator|.
name|parent
expr_stmt|;
name|corrected
operator|--
expr_stmt|;
block|}
while|while
condition|(
name|corrected
operator|<
literal|0
condition|)
block|{
name|other
operator|=
name|other
operator|.
name|parent
expr_stmt|;
name|corrected
operator|++
expr_stmt|;
block|}
name|int
name|cp
init|=
name|comparePath
argument_list|(
name|t
argument_list|,
name|other
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
operator|!=
literal|0
condition|)
block|{
return|return
name|cp
return|;
block|}
return|return
name|Integer
operator|.
name|signum
argument_list|(
name|off
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|"/"
return|;
block|}
else|else
block|{
return|return
name|buildPath
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|length
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|hash
decl_stmt|;
if|if
condition|(
name|h
operator|==
operator|-
literal|1
operator|&&
name|parent
operator|!=
literal|null
condition|)
block|{
name|h
operator|=
literal|17
expr_stmt|;
name|h
operator|=
literal|37
operator|*
name|h
operator|+
name|parent
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|37
operator|*
name|h
operator|+
name|name
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|h
expr_stmt|;
block|}
return|return
name|h
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|Path
condition|)
block|{
name|Path
name|other
init|=
operator|(
name|Path
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|parent
argument_list|,
name|other
operator|.
name|parent
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|//-------------------------< internal>-------------------------------------
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|elements
parameter_list|(
name|boolean
name|withRoot
parameter_list|)
block|{
name|int
name|size
init|=
name|getNumberOfPathElements
argument_list|(
name|withRoot
argument_list|)
decl_stmt|;
name|String
index|[]
name|elements
init|=
operator|new
name|String
index|[
name|size
index|]
decl_stmt|;
name|Path
name|p
init|=
name|this
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
operator|-
literal|1
init|;
name|p
operator|!=
literal|null
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|withRoot
operator|||
operator|!
name|p
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|elements
index|[
name|i
index|]
operator|=
name|p
operator|.
name|name
expr_stmt|;
block|}
name|p
operator|=
name|p
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|elements
argument_list|)
return|;
block|}
specifier|private
name|StringBuilder
name|buildPath
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|buildPath
argument_list|(
name|sb
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|sb
return|;
block|}
comment|/**      * Returns the number of path elements. Depending on {@code withRoot} the      * root of an absolute path is also taken into account.      *      * @param withRoot whether the root of an absolute path is also counted.      * @return the number of path elements.      */
specifier|private
name|int
name|getNumberOfPathElements
parameter_list|(
name|boolean
name|withRoot
parameter_list|)
block|{
name|int
name|depth
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Path
name|p
init|=
name|this
init|;
name|p
operator|!=
literal|null
condition|;
name|p
operator|=
name|p
operator|.
name|parent
control|)
block|{
if|if
condition|(
name|withRoot
operator|||
operator|!
name|p
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|depth
operator|++
expr_stmt|;
block|}
block|}
return|return
name|depth
return|;
block|}
specifier|private
specifier|static
name|int
name|comparePath
parameter_list|(
name|Path
name|a
parameter_list|,
name|Path
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|parent
operator|!=
name|b
operator|.
name|parent
condition|)
block|{
name|int
name|cp
init|=
name|comparePath
argument_list|(
name|a
operator|.
name|parent
argument_list|,
name|b
operator|.
name|parent
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
operator|!=
literal|0
condition|)
block|{
return|return
name|cp
return|;
block|}
block|}
return|return
name|a
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

