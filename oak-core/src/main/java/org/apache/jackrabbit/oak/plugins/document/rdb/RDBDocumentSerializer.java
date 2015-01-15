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
name|rdb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|Map
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
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|Collection
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
name|Document
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
name|DocumentStore
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
name|DocumentStoreException
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
name|NodeDocument
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
name|StableRevisionComparator
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
name|UpdateOp
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
name|UpdateOp
operator|.
name|Key
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
name|UpdateOp
operator|.
name|Operation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|ParseException
import|;
end_import

begin_comment
comment|/**  * Serialization/Parsing of documents.  */
end_comment

begin_class
specifier|public
class|class
name|RDBDocumentSerializer
block|{
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|columnProperties
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MODIFIED
init|=
literal|"_modified"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MODCOUNT
init|=
literal|"_modCount"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CMODCOUNT
init|=
literal|"_collisionsModCount"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"_id"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HASBINARY
init|=
name|NodeDocument
operator|.
name|HAS_BINARY_FLAG
decl_stmt|;
specifier|private
specifier|final
name|Comparator
argument_list|<
name|Revision
argument_list|>
name|comparator
init|=
name|StableRevisionComparator
operator|.
name|REVERSE
decl_stmt|;
specifier|public
name|RDBDocumentSerializer
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|columnProperties
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|columnProperties
operator|=
name|columnProperties
expr_stmt|;
block|}
comment|/**      * Serializes all non-column properties of the {@link Document} into      * a JSON string.      */
specifier|public
name|String
name|asString
parameter_list|(
annotation|@
name|Nonnull
name|Document
name|doc
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|32768
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|boolean
name|needComma
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|doc
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|columnProperties
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
if|if
condition|(
name|needComma
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|appendMember
argument_list|(
name|sb
argument_list|,
name|key
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Serializes the changes in the {@link UpdateOp} into a JSON array; each      * entry is another JSON array holding operation, key, revision, and value.      */
specifier|public
name|String
name|asString
parameter_list|(
name|UpdateOp
name|update
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
name|boolean
name|needComma
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|change
range|:
name|update
operator|.
name|getChanges
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Operation
name|op
init|=
name|change
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Key
name|key
init|=
name|change
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|// exclude properties that are serialized into special columns
if|if
condition|(
name|columnProperties
operator|.
name|contains
argument_list|(
name|key
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
literal|null
operator|==
name|key
operator|.
name|getRevision
argument_list|()
condition|)
continue|continue;
comment|// already checked
if|if
condition|(
name|op
operator|.
name|type
operator|==
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|CONTAINS_MAP_ENTRY
condition|)
continue|continue;
if|if
condition|(
name|needComma
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|.
name|type
operator|==
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|INCREMENT
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\"+\","
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|op
operator|.
name|type
operator|==
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|SET
operator|||
name|op
operator|.
name|type
operator|==
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|SET_MAP_ENTRY
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\"=\","
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|op
operator|.
name|type
operator|==
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|MAX
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\"M\","
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|op
operator|.
name|type
operator|==
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|REMOVE_MAP_ENTRY
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\"*\","
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"Can't serialize "
operator|+
name|update
operator|.
name|toString
argument_list|()
operator|+
literal|" for JSON append"
argument_list|)
throw|;
block|}
name|appendString
argument_list|(
name|sb
argument_list|,
name|key
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|getRevision
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|appendString
argument_list|(
name|sb
argument_list|,
name|key
operator|.
name|getRevision
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|appendValue
argument_list|(
name|sb
argument_list|,
name|op
operator|.
name|value
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|appendMember
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|appendString
argument_list|(
name|sb
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|appendValue
argument_list|(
name|sb
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|appendValue
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|appendString
argument_list|(
name|sb
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
name|appendMap
argument_list|(
name|sb
argument_list|,
operator|(
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"unexpected type: "
operator|+
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|appendMap
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|boolean
name|needComma
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
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
name|needComma
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|appendMember
argument_list|(
name|sb
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|true
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
index|[]
name|JSONCONTROLS
init|=
operator|new
name|String
index|[]
block|{
literal|"\\u0000"
block|,
literal|"\\u0001"
block|,
literal|"\\u0002"
block|,
literal|"\\u0003"
block|,
literal|"\\u0004"
block|,
literal|"\\u0005"
block|,
literal|"\\u0006"
block|,
literal|"\\u0007"
block|,
literal|"\\b"
block|,
literal|"\\t"
block|,
literal|"\\n"
block|,
literal|"\\u000b"
block|,
literal|"\\f"
block|,
literal|"\\r"
block|,
literal|"\\u000e"
block|,
literal|"\\u000f"
block|,
literal|"\\u0010"
block|,
literal|"\\u0011"
block|,
literal|"\\u0012"
block|,
literal|"\\u0013"
block|,
literal|"\\u0014"
block|,
literal|"\\u0015"
block|,
literal|"\\u0016"
block|,
literal|"\\u0017"
block|,
literal|"\\u0018"
block|,
literal|"\\u0019"
block|,
literal|"\\u001a"
block|,
literal|"\\u001b"
block|,
literal|"\\u001c"
block|,
literal|"\\u001d"
block|,
literal|"\\u001e"
block|,
literal|"\\u001f"
block|}
decl_stmt|;
specifier|private
specifier|static
name|void
name|appendString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|int
name|length
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
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
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|ic
init|=
operator|(
name|int
operator|)
name|c
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'"'
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\\\""
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\\\\"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|0
operator|&&
name|c
operator|<
literal|0x20
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|JSONCONTROLS
index|[
name|c
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ic
operator|>=
literal|0xD800
operator|&&
name|ic
operator|<=
literal|0xDBFF
condition|)
block|{
comment|// isSurrogate(), only available in Java 7
if|if
condition|(
name|i
operator|<
name|length
operator|-
literal|1
operator|&&
name|Character
operator|.
name|isSurrogatePair
argument_list|(
name|c
argument_list|,
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
condition|)
block|{
comment|// ok surrogate
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// broken surrogate -> escape
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\\u%04X"
argument_list|,
name|ic
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reconstructs a {@link Document) based on the persisted {@link DBRow}.      */
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|fromRow
parameter_list|(
annotation|@
name|Nonnull
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
annotation|@
name|Nonnull
name|RDBRow
name|row
parameter_list|)
throws|throws
name|DocumentStoreException
block|{
name|T
name|doc
init|=
name|collection
operator|.
name|newDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|ID
argument_list|,
name|row
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|MODIFIED
argument_list|,
name|row
operator|.
name|getModified
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|MODCOUNT
argument_list|,
name|row
operator|.
name|getModcount
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|CMODCOUNT
argument_list|,
name|row
operator|.
name|getCollisionsModcount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|row
operator|.
name|hasBinaryProperties
argument_list|()
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|HASBINARY
argument_list|,
name|NodeDocument
operator|.
name|HAS_BINARY_VAL
argument_list|)
expr_stmt|;
block|}
name|JSONParser
name|jp
init|=
operator|new
name|JSONParser
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|baseData
init|=
literal|null
decl_stmt|;
name|byte
index|[]
name|bdata
init|=
name|row
operator|.
name|getBdata
argument_list|()
decl_stmt|;
name|JSONArray
name|arr
init|=
literal|null
decl_stmt|;
name|int
name|updatesStartAt
init|=
literal|0
decl_stmt|;
comment|// case #1: BDATA (blob) contains base data, DATA (string) contains
comment|// update operations
try|try
block|{
if|if
condition|(
name|bdata
operator|!=
literal|null
operator|&&
name|bdata
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|baseData
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|jp
operator|.
name|parse
argument_list|(
name|fromBlobData
argument_list|(
name|bdata
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO figure out a faster way
name|arr
operator|=
operator|(
name|JSONArray
operator|)
operator|new
name|JSONParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"["
operator|+
name|row
operator|.
name|getData
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
comment|// case #2: if we do not have BDATA (blob), the first part of DATA
comment|// (string) already is the base data, and update operations can follow
if|if
condition|(
name|baseData
operator|==
literal|null
condition|)
block|{
name|baseData
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|updatesStartAt
operator|=
literal|1
expr_stmt|;
block|}
comment|// process the base data
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|baseData
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// TODO ???
name|doc
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Boolean
operator|||
name|value
operator|instanceof
name|Long
operator|||
name|value
operator|instanceof
name|String
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|JSONObject
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|convertJsonObject
argument_list|(
operator|(
name|JSONObject
operator|)
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"unexpected type: "
operator|+
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
for|for
control|(
name|int
name|u
init|=
name|updatesStartAt
init|;
name|u
operator|<
name|arr
operator|.
name|size
argument_list|()
condition|;
name|u
operator|++
control|)
block|{
name|Object
name|ob
init|=
name|arr
operator|.
name|get
argument_list|(
name|u
argument_list|)
decl_stmt|;
if|if
condition|(
name|ob
operator|instanceof
name|JSONArray
condition|)
block|{
name|JSONArray
name|update
init|=
operator|(
name|JSONArray
operator|)
name|ob
decl_stmt|;
for|for
control|(
name|int
name|o
init|=
literal|0
init|;
name|o
operator|<
name|update
operator|.
name|size
argument_list|()
condition|;
name|o
operator|++
control|)
block|{
name|JSONArray
name|op
init|=
operator|(
name|JSONArray
operator|)
name|update
operator|.
name|get
argument_list|(
name|o
argument_list|)
decl_stmt|;
name|applyUpdate
argument_list|(
name|doc
argument_list|,
name|update
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ob
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"blob"
argument_list|)
operator|&&
name|u
operator|==
literal|0
condition|)
block|{
comment|// expected placeholder
block|}
else|else
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"unexpected JSON in DATA column: "
operator|+
name|ob
argument_list|)
throw|;
block|}
block|}
return|return
name|doc
return|;
block|}
specifier|private
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|applyUpdate
parameter_list|(
name|T
name|doc
parameter_list|,
name|JSONArray
name|update
parameter_list|,
name|JSONArray
name|op
parameter_list|)
block|{
name|String
name|opcode
init|=
name|op
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|op
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Revision
name|rev
init|=
literal|null
decl_stmt|;
name|Object
name|value
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
name|value
operator|=
name|op
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rev
operator|=
name|Revision
operator|.
name|fromString
argument_list|(
name|op
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|=
name|op
operator|.
name|get
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
name|Object
name|old
init|=
name|doc
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"="
operator|.
name|equals
argument_list|(
name|opcode
argument_list|)
condition|)
block|{
if|if
condition|(
name|rev
operator|==
literal|null
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|(
name|Map
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
operator|)
name|old
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
block|{
name|m
operator|=
operator|new
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
argument_list|(
name|comparator
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|put
argument_list|(
name|rev
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|opcode
argument_list|)
condition|)
block|{
if|if
condition|(
name|rev
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"unexpected operation "
operator|+
name|op
operator|+
literal|" in: "
operator|+
name|update
argument_list|)
throw|;
block|}
else|else
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|(
name|Map
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
operator|)
name|old
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|remove
argument_list|(
name|rev
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"+"
operator|.
name|equals
argument_list|(
name|opcode
argument_list|)
condition|)
block|{
if|if
condition|(
name|rev
operator|==
literal|null
condition|)
block|{
name|Long
name|x
init|=
operator|(
name|Long
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|old
operator|=
literal|0L
expr_stmt|;
block|}
name|doc
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|old
operator|)
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"unexpected operation "
operator|+
name|op
operator|+
literal|" in: "
operator|+
name|update
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"M"
operator|.
name|equals
argument_list|(
name|opcode
argument_list|)
condition|)
block|{
if|if
condition|(
name|rev
operator|==
literal|null
condition|)
block|{
name|Comparable
name|newValue
init|=
operator|(
name|Comparable
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
operator|||
name|newValue
operator|.
name|compareTo
argument_list|(
name|old
argument_list|)
operator|>
literal|0
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"unexpected operation "
operator|+
name|op
operator|+
literal|" in: "
operator|+
name|update
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"unexpected operation "
operator|+
name|op
operator|+
literal|" in: "
operator|+
name|update
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|Map
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
name|convertJsonObject
parameter_list|(
annotation|@
name|Nonnull
name|JSONObject
name|obj
parameter_list|)
block|{
name|Map
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|Object
argument_list|>
argument_list|(
name|comparator
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
operator|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
operator|)
name|obj
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|// low level operations
specifier|private
specifier|static
name|byte
index|[]
name|GZIPSIG
init|=
block|{
literal|31
block|,
operator|-
literal|117
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
name|fromBlobData
parameter_list|(
name|byte
index|[]
name|bdata
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|bdata
operator|.
name|length
operator|>=
literal|2
operator|&&
name|bdata
index|[
literal|0
index|]
operator|==
name|GZIPSIG
index|[
literal|0
index|]
operator|&&
name|bdata
index|[
literal|1
index|]
operator|==
name|GZIPSIG
index|[
literal|1
index|]
condition|)
block|{
comment|// GZIP
name|ByteArrayInputStream
name|bis
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bdata
argument_list|)
decl_stmt|;
name|GZIPInputStream
name|gis
init|=
operator|new
name|GZIPInputStream
argument_list|(
name|bis
argument_list|,
literal|65536
argument_list|)
decl_stmt|;
return|return
name|IOUtils
operator|.
name|toString
argument_list|(
name|gis
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|IOUtils
operator|.
name|toString
argument_list|(
name|bdata
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

