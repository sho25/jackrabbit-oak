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
name|remote
operator|.
name|content
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|Blob
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
name|Root
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
name|Type
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
name|remote
operator|.
name|RemoteValue
operator|.
name|Supplier
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
name|remote
operator|.
name|RemoteValue
operator|.
name|TypeHandler
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
name|ISO8601
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

begin_class
class|class
name|SetPropertyHandler
extends|extends
name|TypeHandler
block|{
specifier|private
specifier|final
name|ContentRemoteBinaries
name|binaries
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|tree
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|SetPropertyHandler
parameter_list|(
name|ContentRemoteBinaries
name|binaries
parameter_list|,
name|Root
name|root
parameter_list|,
name|Tree
name|tree
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|binaries
operator|=
name|binaries
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isBinary
parameter_list|(
name|Supplier
argument_list|<
name|InputStream
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|getBlob
argument_list|(
name|root
argument_list|,
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|BINARY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiBinary
parameter_list|(
name|Iterable
argument_list|<
name|Supplier
argument_list|<
name|InputStream
argument_list|>
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|getBlobs
argument_list|(
name|root
argument_list|,
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|BINARIES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isBinaryId
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|getBlobFromId
argument_list|(
name|binaries
argument_list|,
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|BINARY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiBinaryId
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|getBlobsFromIds
argument_list|(
name|binaries
argument_list|,
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|BINARIES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isBoolean
parameter_list|(
name|Boolean
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiBoolean
parameter_list|(
name|Iterable
argument_list|<
name|Boolean
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|BOOLEANS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isDate
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|getDate
argument_list|(
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiDate
parameter_list|(
name|Iterable
argument_list|<
name|Long
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|getDates
argument_list|(
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|DATES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isDecimal
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|DECIMAL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiDecimal
parameter_list|(
name|Iterable
argument_list|<
name|BigDecimal
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|DECIMALS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isDouble
parameter_list|(
name|Double
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiDouble
parameter_list|(
name|Iterable
argument_list|<
name|Double
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|DOUBLES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isLong
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiLong
parameter_list|(
name|Iterable
argument_list|<
name|Long
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|LONGS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isName
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiName
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isPath
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiPath
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|PATHS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isReference
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiReference
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|REFERENCES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isText
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiText
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isUri
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|URI
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiUri
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|URIS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isWeakReference
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|WEAKREFERENCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isMultiWeakReference
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|WEAKREFERENCES
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Blob
name|getBlob
parameter_list|(
name|Root
name|root
parameter_list|,
name|Supplier
argument_list|<
name|InputStream
argument_list|>
name|supplier
parameter_list|)
block|{
name|InputStream
name|inputStream
init|=
name|supplier
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|inputStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"invalid input stream"
argument_list|)
throw|;
block|}
name|Blob
name|blob
decl_stmt|;
try|try
block|{
name|blob
operator|=
name|root
operator|.
name|createBlob
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unable to create a blob"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|blob
return|;
block|}
specifier|private
name|Iterable
argument_list|<
name|Blob
argument_list|>
name|getBlobs
parameter_list|(
specifier|final
name|Root
name|root
parameter_list|,
name|Iterable
argument_list|<
name|Supplier
argument_list|<
name|InputStream
argument_list|>
argument_list|>
name|suppliers
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|suppliers
argument_list|,
operator|new
name|Function
argument_list|<
name|Supplier
argument_list|<
name|InputStream
argument_list|>
argument_list|,
name|Blob
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Blob
name|apply
parameter_list|(
name|Supplier
argument_list|<
name|InputStream
argument_list|>
name|supplier
parameter_list|)
block|{
return|return
name|getBlob
argument_list|(
name|root
argument_list|,
name|supplier
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|Blob
name|getBlobFromId
parameter_list|(
name|ContentRemoteBinaries
name|binaries
parameter_list|,
name|String
name|binaryId
parameter_list|)
block|{
return|return
name|binaries
operator|.
name|get
argument_list|(
name|binaryId
argument_list|)
return|;
block|}
specifier|private
name|Iterable
argument_list|<
name|Blob
argument_list|>
name|getBlobsFromIds
parameter_list|(
specifier|final
name|ContentRemoteBinaries
name|binaries
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|binaryIds
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|binaryIds
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Blob
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Blob
name|apply
parameter_list|(
name|String
name|binaryId
parameter_list|)
block|{
return|return
name|getBlobFromId
argument_list|(
name|binaries
argument_list|,
name|binaryId
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|String
name|getDate
parameter_list|(
name|Long
name|time
parameter_list|)
block|{
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|setTimeInMillis
argument_list|(
name|time
argument_list|)
expr_stmt|;
return|return
name|ISO8601
operator|.
name|format
argument_list|(
name|calendar
argument_list|)
return|;
block|}
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|getDates
parameter_list|(
name|Iterable
argument_list|<
name|Long
argument_list|>
name|times
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|times
argument_list|,
operator|new
name|Function
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Long
name|time
parameter_list|)
block|{
return|return
name|getDate
argument_list|(
name|time
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

