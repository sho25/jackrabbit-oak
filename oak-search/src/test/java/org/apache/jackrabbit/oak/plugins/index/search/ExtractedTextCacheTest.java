begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|index
operator|.
name|search
package|;
end_package

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
name|FileUtils
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
name|plugins
operator|.
name|index
operator|.
name|fulltext
operator|.
name|ExtractedText
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
name|index
operator|.
name|fulltext
operator|.
name|ExtractedText
operator|.
name|ExtractionResult
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
name|index
operator|.
name|fulltext
operator|.
name|PreExtractedTextProvider
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
name|index
operator|.
name|search
operator|.
name|spi
operator|.
name|editor
operator|.
name|FulltextIndexEditor
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
name|memory
operator|.
name|ArrayBasedBlob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verifyZeroInteractions
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|ExtractedTextCacheTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|cacheDisabling
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getCacheStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cacheEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cache
operator|.
name|getCacheStats
argument_list|()
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|b
argument_list|,
operator|new
name|ExtractedText
argument_list|(
name|ExtractionResult
operator|.
name|SUCCESS
argument_list|,
literal|"test hello"
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test hello"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cacheEnabledNonIdBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Blob
name|b
init|=
operator|new
name|ArrayBasedBlob
argument_list|(
literal|"hello"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|b
argument_list|,
operator|new
name|ExtractedText
argument_list|(
name|ExtractionResult
operator|.
name|SUCCESS
argument_list|,
literal|"test hello"
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cacheEnabledErrorInTextExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|b
argument_list|,
operator|new
name|ExtractedText
argument_list|(
name|ExtractionResult
operator|.
name|ERROR
argument_list|,
literal|"test hello"
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FulltextIndexEditor
operator|.
name|TEXT_EXTRACTION_ERROR
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|preExtractionNoReindexNoProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|preExtractionNoReindex
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|PreExtractedTextProvider
name|provider
init|=
name|mock
argument_list|(
name|PreExtractedTextProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|cache
operator|.
name|setExtractedTextProvider
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|preExtractionReindex
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|PreExtractedTextProvider
name|provider
init|=
name|mock
argument_list|(
name|PreExtractedTextProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|cache
operator|.
name|setExtractedTextProvider
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|provider
operator|.
name|getText
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|Blob
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ExtractedText
argument_list|(
name|ExtractionResult
operator|.
name|SUCCESS
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|preExtractionAlwaysUse
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|PreExtractedTextProvider
name|provider
init|=
name|mock
argument_list|(
name|PreExtractedTextProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|cache
operator|.
name|setExtractedTextProvider
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|provider
operator|.
name|getText
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|Blob
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ExtractedText
argument_list|(
name|ExtractionResult
operator|.
name|SUCCESS
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rememberTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|ExtractedText
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|putTimeout
argument_list|(
name|b
argument_list|,
name|ExtractedText
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FulltextIndexEditor
operator|.
name|TEXT_EXTRACTION_ERROR
argument_list|,
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|cache
operator|.
name|process
argument_list|(
literal|"test"
argument_list|,
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|OutOfMemoryError
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cache
operator|.
name|getStatsMBean
argument_list|()
operator|.
name|getTimeoutCount
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|setExtractionTimeoutMillis
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|cache
operator|.
name|process
argument_list|(
literal|"test"
argument_list|,
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this happens in the background, so doesn't block the test
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
name|assertTrue
argument_list|(
literal|""
operator|+
name|time
argument_list|,
name|time
operator|<
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cache
operator|.
name|getStatsMBean
argument_list|()
operator|.
name|getTimeoutCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nullContentIdentityBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|ExtractedText
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|putTimeout
argument_list|(
name|b
argument_list|,
name|ExtractedText
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Cache returned non null text for blob with null content identity"
argument_list|,
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|IdBlob
extends|extends
name|ArrayBasedBlob
block|{
specifier|final
name|String
name|id
decl_stmt|;
specifier|public
name|IdBlob
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
name|id
return|;
block|}
block|}
block|}
end_class

end_unit

