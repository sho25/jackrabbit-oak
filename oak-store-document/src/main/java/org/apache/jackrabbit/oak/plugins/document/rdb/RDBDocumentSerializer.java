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
operator|.
name|RDBJDBCTools
operator|.
name|asDocumentStoreException
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBJSONSupport
operator|.
name|appendJsonMember
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBJSONSupport
operator|.
name|appendJsonString
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBJSONSupport
operator|.
name|appendJsonValue
import|;
end_import

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
name|List
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
name|commons
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
name|oak
operator|.
name|commons
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
specifier|static
specifier|final
name|String
name|MODIFIED
init|=
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MODCOUNT
init|=
name|NodeDocument
operator|.
name|MOD_COUNT
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
name|SDTYPE
init|=
name|NodeDocument
operator|.
name|SD_TYPE
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SDMAXREVTIME
init|=
name|NodeDocument
operator|.
name|SD_MAX_REV_TIME_IN_SECS
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
specifier|static
specifier|final
name|String
name|DELETEDONCE
init|=
name|NodeDocument
operator|.
name|DELETED_ONCE
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
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RDBDocumentSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|RDBJSONSupport
name|JSON
init|=
operator|new
name|RDBJSONSupport
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|public
name|RDBDocumentSerializer
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
comment|/**      * Serializes all non-column properties of the {@link Document} into a JSON      * string.      */
specifier|public
name|String
name|asString
parameter_list|(
annotation|@
name|NotNull
name|Document
name|doc
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|columnProperties
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
name|doc
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
name|appendJsonMember
argument_list|(
name|sb
argument_list|,
name|key
argument_list|,
name|entry
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
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|columnProperties
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
name|REMOVE
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
name|appendJsonString
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
name|Revision
name|rev
init|=
name|key
operator|.
name|getRevision
argument_list|()
decl_stmt|;
if|if
condition|(
name|rev
operator|!=
literal|null
condition|)
block|{
name|appendJsonString
argument_list|(
name|sb
argument_list|,
name|rev
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
name|appendJsonValue
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
comment|/**      * Reconstructs a {@link Document} based on the persisted {@link RDBRow}.      */
annotation|@
name|NotNull
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
name|NotNull
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
annotation|@
name|NotNull
name|RDBRow
name|row
parameter_list|)
throws|throws
name|DocumentStoreException
block|{
specifier|final
name|String
name|charData
init|=
name|row
operator|.
name|getData
argument_list|()
decl_stmt|;
name|checkNotNull
argument_list|(
name|charData
argument_list|,
literal|"RDBRow.getData() is null for collection "
operator|+
name|collection
operator|+
literal|", id: "
operator|+
name|row
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|row
operator|.
name|getModified
argument_list|()
operator|!=
name|RDBRow
operator|.
name|LONG_UNSET
condition|)
block|{
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
block|}
if|if
condition|(
name|row
operator|.
name|getModcount
argument_list|()
operator|!=
name|RDBRow
operator|.
name|LONG_UNSET
condition|)
block|{
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
block|}
if|if
condition|(
name|RDBDocumentStore
operator|.
name|USECMODCOUNT
operator|&&
name|row
operator|.
name|getCollisionsModcount
argument_list|()
operator|!=
name|RDBRow
operator|.
name|LONG_UNSET
condition|)
block|{
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
block|}
if|if
condition|(
name|row
operator|.
name|hasBinaryProperties
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|HASBINARY
argument_list|,
name|row
operator|.
name|hasBinaryProperties
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|row
operator|.
name|deletedOnce
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|DELETEDONCE
argument_list|,
name|row
operator|.
name|deletedOnce
argument_list|()
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|row
operator|.
name|getSchemaVersion
argument_list|()
operator|>=
literal|2
condition|)
block|{
if|if
condition|(
name|row
operator|.
name|getSdType
argument_list|()
operator|!=
name|RDBRow
operator|.
name|LONG_UNSET
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|SDTYPE
argument_list|,
name|row
operator|.
name|getSdType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|row
operator|.
name|getSdMaxRevTime
argument_list|()
operator|!=
name|RDBRow
operator|.
name|LONG_UNSET
condition|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|SDMAXREVTIME
argument_list|,
name|row
operator|.
name|getSdMaxRevTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|byte
index|[]
name|bdata
init|=
name|row
operator|.
name|getBdata
argument_list|()
decl_stmt|;
name|boolean
name|blobInUse
init|=
literal|false
decl_stmt|;
name|JsopTokenizer
name|json
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
name|String
name|s
init|=
name|fromBlobData
argument_list|(
name|bdata
argument_list|)
decl_stmt|;
name|json
operator|=
operator|new
name|JsopTokenizer
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|json
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|readDocumentFromJson
argument_list|(
name|json
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|json
operator|.
name|read
argument_list|(
name|JsopReader
operator|.
name|END
argument_list|)
expr_stmt|;
name|blobInUse
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
name|asDocumentStoreException
argument_list|(
name|ex
argument_list|,
literal|"parsing blob data as JSON"
argument_list|)
throw|;
block|}
name|json
operator|=
operator|new
name|JsopTokenizer
argument_list|(
name|charData
argument_list|)
expr_stmt|;
comment|// start processing the VARCHAR data
try|try
block|{
name|int
name|next
init|=
name|json
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|==
literal|'{'
condition|)
block|{
if|if
condition|(
name|blobInUse
condition|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"expected literal \"blob\" but found: "
operator|+
name|row
operator|.
name|getData
argument_list|()
argument_list|)
throw|;
block|}
name|readDocumentFromJson
argument_list|(
name|json
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|next
operator|==
name|JsopReader
operator|.
name|STRING
condition|)
block|{
if|if
condition|(
operator|!
name|blobInUse
condition|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"did not expect \"blob\" here: "
operator|+
name|row
operator|.
name|getData
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
literal|"blob"
operator|.
name|equals
argument_list|(
name|json
operator|.
name|getToken
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"expected string literal \"blob\""
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
literal|"unexpected token "
operator|+
name|next
operator|+
literal|" in "
operator|+
name|row
operator|.
name|getData
argument_list|()
argument_list|)
throw|;
block|}
name|next
operator|=
name|json
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|==
literal|','
condition|)
block|{
do|do
block|{
name|Object
name|ob
init|=
name|JSON
operator|.
name|parse
argument_list|(
name|json
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|ob
operator|instanceof
name|List
operator|)
condition|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"expected array but got: "
operator|+
name|ob
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|update
init|=
operator|(
name|List
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|ob
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|Object
argument_list|>
name|op
range|:
name|update
control|)
block|{
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
do|while
condition|(
name|json
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
block|}
name|json
operator|.
name|read
argument_list|(
name|JsopReader
operator|.
name|END
argument_list|)
expr_stmt|;
comment|// OAK-7855: check and fix _sdType
name|checkSdType
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Error processing persisted data for document '%s'"
argument_list|,
name|row
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|charData
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|last
init|=
name|charData
operator|.
name|charAt
argument_list|(
name|charData
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|'}'
operator|&&
name|last
operator|!=
literal|'"'
operator|&&
name|last
operator|!=
literal|']'
condition|)
block|{
name|message
operator|+=
literal|" (DATA column might be truncated)"
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|asDocumentStoreException
argument_list|(
name|ex
argument_list|,
name|message
argument_list|)
throw|;
block|}
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
name|List
name|updateString
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
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
name|doc
operator|.
name|remove
argument_list|(
name|key
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
name|updateString
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
name|updateString
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
name|updateString
argument_list|)
throw|;
block|}
block|}
comment|/**      * Reads from an opened JSON stream ("{" already consumed) into a document.      */
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|readDocumentFromJson
parameter_list|(
annotation|@
name|NotNull
name|JsopTokenizer
name|json
parameter_list|,
annotation|@
name|NotNull
name|T
name|doc
parameter_list|)
block|{
if|if
condition|(
operator|!
name|json
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
do|do
block|{
name|String
name|key
init|=
name|json
operator|.
name|readString
argument_list|()
decl_stmt|;
name|json
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|JSON
operator|.
name|parse
argument_list|(
name|json
argument_list|)
decl_stmt|;
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
do|while
condition|(
name|json
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|json
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|checkSdType
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|Object
name|sdType
init|=
name|doc
operator|.
name|get
argument_list|(
name|NodeDocument
operator|.
name|SD_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|sdType
operator|instanceof
name|Long
condition|)
block|{
name|long
name|value
init|=
operator|(
name|long
operator|)
name|sdType
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
name|doc
operator|.
name|remove
argument_list|(
name|NodeDocument
operator|.
name|SD_TYPE
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Incorrect _sdType 0 in {}"
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
try|try
init|(
name|GZIPInputStream
name|gis
init|=
operator|new
name|GZIPInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bdata
argument_list|)
argument_list|,
literal|65536
argument_list|)
init|)
block|{
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
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unexpected exception while processing blob data"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

