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
name|plugins
operator|.
name|document
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
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
name|plugins
operator|.
name|document
operator|.
name|Revision
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|types
operator|.
name|ObjectId
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

begin_comment
comment|/**  * Utility methods.  */
end_comment

begin_class
specifier|public
class|class
name|Utils
block|{
comment|/**      * Approximate length of a Revision string.      */
specifier|private
specifier|static
specifier|final
name|int
name|REVISION_LENGTH
init|=
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
comment|/**      * Make sure the name string does not contain unnecessary baggage (shared      * strings).      *<p>      * This is only needed for older versions of Java (before Java 7 update 6).      * See also      * http://mail.openjdk.java.net/pipermail/core-libs-dev/2012-May/010257.html      *       * @param x the string      * @return the new string      */
specifier|public
specifier|static
name|String
name|unshareString
parameter_list|(
name|String
name|x
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|x
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|pathDepth
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|depth
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
name|path
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'/'
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
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newMap
parameter_list|()
block|{
return|return
operator|new
name|TreeMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|newSet
parameter_list|()
block|{
return|return
operator|new
name|HashSet
argument_list|<
name|E
argument_list|>
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
name|int
name|estimateMemoryUsage
parameter_list|(
name|Map
argument_list|<
name|?
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|?
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|instanceof
name|Revision
condition|)
block|{
name|size
operator|+=
literal|32
expr_stmt|;
block|}
else|else
block|{
name|size
operator|+=
literal|48
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|*
literal|2
expr_stmt|;
block|}
name|Object
name|o
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|size
operator|+=
literal|48
operator|+
operator|(
operator|(
name|String
operator|)
name|o
operator|)
operator|.
name|length
argument_list|()
operator|*
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Long
condition|)
block|{
name|size
operator|+=
literal|16
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Boolean
condition|)
block|{
name|size
operator|+=
literal|8
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Integer
condition|)
block|{
name|size
operator|+=
literal|8
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|size
operator|+=
literal|8
operator|+
name|estimateMemoryUsage
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
comment|// zero
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't estimate memory usage of "
operator|+
name|o
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|map
operator|instanceof
name|BasicDBObject
condition|)
block|{
comment|// Based on empirical testing using JAMM
name|size
operator|+=
literal|176
expr_stmt|;
name|size
operator|+=
name|map
operator|.
name|size
argument_list|()
operator|*
literal|136
expr_stmt|;
block|}
else|else
block|{
comment|// overhead for some other kind of map
comment|// TreeMap (80) + unmodifiable wrapper (32)
name|size
operator|+=
literal|112
expr_stmt|;
comment|// 64 bytes per entry
name|size
operator|+=
name|map
operator|.
name|size
argument_list|()
operator|*
literal|64
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
comment|/**      * Generate a unique cluster id, similar to the machine id field in MongoDB ObjectId objects.      *       * @return the unique machine id      */
specifier|public
specifier|static
name|int
name|getUniqueClusterId
parameter_list|()
block|{
name|ObjectId
name|objId
init|=
operator|new
name|ObjectId
argument_list|()
decl_stmt|;
return|return
name|objId
operator|.
name|_machine
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|escapePropertyName
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
name|int
name|len
init|=
name|propertyName
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
return|return
literal|"_"
return|;
block|}
comment|// avoid creating a buffer if escaping is not needed
name|StringBuilder
name|buff
init|=
literal|null
decl_stmt|;
name|char
name|c
init|=
name|propertyName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'_'
operator|||
name|c
operator|==
literal|'$'
condition|)
block|{
name|buff
operator|=
operator|new
name|StringBuilder
argument_list|(
name|len
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
for|for
control|(
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
name|propertyName
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|char
name|rep
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'.'
case|:
name|rep
operator|=
literal|'d'
expr_stmt|;
break|break;
case|case
literal|'\\'
case|:
name|rep
operator|=
literal|'\\'
expr_stmt|;
break|break;
default|default:
name|rep
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|rep
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|buff
operator|==
literal|null
condition|)
block|{
name|buff
operator|=
operator|new
name|StringBuilder
argument_list|(
name|propertyName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
operator|.
name|append
argument_list|(
name|rep
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buff
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buff
operator|==
literal|null
condition|?
name|propertyName
else|:
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|unescapePropertyName
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|int
name|len
init|=
name|key
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
operator|&&
operator|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"__"
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
literal|"_$"
argument_list|)
operator|||
name|len
operator|==
literal|1
operator|)
condition|)
block|{
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|len
operator|--
expr_stmt|;
block|}
comment|// avoid creating a buffer if escaping is not needed
name|StringBuilder
name|buff
init|=
literal|null
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
name|key
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
literal|'\\'
condition|)
block|{
if|if
condition|(
name|buff
operator|==
literal|null
condition|)
block|{
name|buff
operator|=
operator|new
name|StringBuilder
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|c
operator|=
name|key
operator|.
name|charAt
argument_list|(
operator|++
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
comment|// ok
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'d'
condition|)
block|{
name|c
operator|=
literal|'.'
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buff
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buff
operator|==
literal|null
condition|?
name|key
else|:
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isPropertyName
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|!
name|key
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
literal|"__"
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
literal|"_$"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getIdFromPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|int
name|depth
init|=
name|Utils
operator|.
name|pathDepth
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|depth
operator|+
literal|":"
operator|+
name|path
return|;
block|}
specifier|public
specifier|static
name|String
name|getPathFromId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|int
name|index
init|=
name|id
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
return|return
name|id
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getPreviousIdFor
parameter_list|(
name|String
name|id
parameter_list|,
name|Revision
name|r
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|id
operator|.
name|length
argument_list|()
operator|+
name|REVISION_LENGTH
operator|+
literal|3
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|id
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|int
name|depth
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
name|index
condition|;
name|i
operator|++
control|)
block|{
name|depth
operator|*=
literal|10
expr_stmt|;
name|depth
operator|+=
name|Character
operator|.
name|digit
argument_list|(
name|id
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|depth
operator|+
literal|1
argument_list|)
operator|.
name|append
argument_list|(
literal|":p"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|id
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|'/'
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
name|r
operator|.
name|toStringBuilder
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Deep copy of a map that may contain map values.      *       * @param source the source map      * @param target the target map      * @param<K> the type of the map key      */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|>
name|void
name|deepCopyMap
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|,
name|Map
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
name|target
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|source
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Comparator
argument_list|<
name|?
super|super
name|K
argument_list|>
name|comparator
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|SortedMap
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|SortedMap
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|SortedMap
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
operator|)
name|value
decl_stmt|;
name|comparator
operator|=
name|map
operator|.
name|comparator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
name|old
init|=
operator|(
name|Map
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
operator|)
name|value
decl_stmt|;
name|Map
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
name|c
init|=
operator|new
name|TreeMap
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
argument_list|(
name|comparator
argument_list|)
decl_stmt|;
name|deepCopyMap
argument_list|(
name|old
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|value
operator|=
name|c
expr_stmt|;
block|}
name|target
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the lower key limit to retrieve the children of the given      *<code>path</code>.      *      * @param path a path.      * @return the lower key limit.      */
specifier|public
specifier|static
name|String
name|getKeyLowerLimit
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|from
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|from
operator|=
name|getIdFromPath
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|from
operator|=
name|from
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|from
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|from
return|;
block|}
comment|/**      * Returns the upper key limit to retrieve the children of the given      *<code>path</code>.      *      * @param path a path.      * @return the upper key limit.      */
specifier|public
specifier|static
name|String
name|getKeyUpperLimit
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|to
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
literal|"z"
argument_list|)
decl_stmt|;
name|to
operator|=
name|getIdFromPath
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|to
operator|=
name|to
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|to
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
operator|+
literal|"0"
expr_stmt|;
return|return
name|to
return|;
block|}
comment|/**      * Returns<code>true</code> if a revision tagged with the given revision      * should be considered committed,<code>false</code> otherwise. Committed      * revisions have a tag, which equals 'c' or starts with 'c-'.      *      * @param tag the tag (may be<code>null</code>).      * @return<code>true</code> if committed;<code>false</code> otherwise.      */
specifier|public
specifier|static
name|boolean
name|isCommitted
parameter_list|(
annotation|@
name|Nullable
name|String
name|tag
parameter_list|)
block|{
return|return
name|tag
operator|!=
literal|null
operator|&&
operator|(
name|tag
operator|.
name|equals
argument_list|(
literal|"c"
argument_list|)
operator|||
name|tag
operator|.
name|startsWith
argument_list|(
literal|"c-"
argument_list|)
operator|)
return|;
block|}
comment|/**      * Resolve the commit revision for the given revision<code>rev</code> and      * the associated commit tag.      *      * @param rev a revision.      * @param tag the associated commit tag.      * @return the actual commit revision for<code>rev</code>.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|Revision
name|resolveCommitRevision
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|rev
parameter_list|,
annotation|@
name|Nonnull
name|String
name|tag
parameter_list|)
block|{
return|return
name|checkNotNull
argument_list|(
name|tag
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"c-"
argument_list|)
condition|?
name|Revision
operator|.
name|fromString
argument_list|(
name|tag
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
else|:
name|rev
return|;
block|}
block|}
end_class

end_unit

