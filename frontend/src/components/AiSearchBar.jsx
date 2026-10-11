import { useState } from 'react';
import { Search, Sparkles, X, BrainCircuit, CornerDownLeft } from 'lucide-react';
import api from '../services/api';

export default function AiSearchBar({ onFilterChange, onAiSearchResult }) {
  const [query, setQuery] = useState('');
  const [aiMode, setAiMode] = useState(false);
  const [loading, setLoading] = useState(false);
  const [aiResponse, setAiResponse] = useState(null);

  const handleSearchSubmit = async (e) => {
    e.preventDefault();
    if (!query.trim()) {
      handleClear();
      return;
    }

    if (!aiMode) {
      onFilterChange(query.trim());
      return;
    }

    // AI Natural Language Search
    setLoading(true);
    setAiResponse(null);
    try {
      const res = await api.post('/ai/search', { query: query.trim() });
      setAiResponse(res.data);
      if (onAiSearchResult) onAiSearchResult(res.data);
    } catch (err) {
      alert('AI Search Error: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setQuery('');
    setAiResponse(null);
    onFilterChange('');
    if (onAiSearchResult) onAiSearchResult(null);
  };

  return (
    <div className="space-y-3">
      {/* Search Input Bar */}
      <form onSubmit={handleSearchSubmit} className="relative flex items-center">
        <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
          {aiMode ? <Sparkles className="w-4 h-4 text-amber-500" /> : <Search className="w-4 h-4" />}
        </div>

        <input
          type="text"
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            if (!aiMode) onFilterChange(e.target.value);
          }}
          placeholder={
            aiMode
              ? 'Ask your documents in plain English (e.g., "What was the total on the Acme bill?")...'
              : 'Search documents by title, tags, or content...'
          }
          className={`w-full pl-10 pr-28 py-3 text-xs sm:text-sm bg-white border rounded-xl shadow-2xs outline-hidden transition-all ${
            aiMode
              ? 'border-amber-300 focus:ring-2 focus:ring-amber-400 focus:border-amber-400'
              : 'border-slate-200 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500'
          }`}
        />

        {/* Clear & AI Toggle Buttons */}
        <div className="absolute inset-y-0 right-0 pr-2 flex items-center space-x-1.5">
          {query && (
            <button
              type="button"
              onClick={handleClear}
              className="p-1 text-slate-400 hover:text-slate-600 rounded-md hover:bg-slate-100 transition-colors cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          )}

          <button
            type="button"
            onClick={() => {
              setAiMode(!aiMode);
              setAiResponse(null);
            }}
            className={`px-2.5 py-1 text-xs font-bold rounded-lg flex items-center gap-1 transition-all cursor-pointer ${
              aiMode
                ? 'bg-amber-100 text-amber-900 border border-amber-300 shadow-2xs'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            <Sparkles className={`w-3.5 h-3.5 ${aiMode ? 'text-amber-600' : 'text-slate-500'}`} />
            <span>AI Mode</span>
          </button>
        </div>
      </form>

      {/* AI Search Loading State */}
      {loading && (
        <div className="p-4 bg-amber-50/70 border border-amber-200 rounded-xl flex items-center gap-3 text-xs text-amber-800 animate-pulse">
          <BrainCircuit className="w-5 h-5 text-amber-600 animate-spin" />
          <span>Searching locker documents and synthesizing natural-language answer with Gemini AI...</span>
        </div>
      )}

      {/* AI Answer Card */}
      {aiResponse && !loading && (
        <div className="p-5 bg-gradient-to-r from-amber-50/80 via-white to-amber-50/50 border border-amber-200 rounded-2xl shadow-xs animate-in fade-in duration-200">
          <div className="flex items-center justify-between pb-2 border-b border-amber-200/60 mb-2.5">
            <div className="flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-amber-600" />
              <span className="text-xs font-bold text-amber-950">AI Document Assistant Answer</span>
            </div>
            <button
              onClick={() => setAiResponse(null)}
              className="text-slate-400 hover:text-slate-600 p-1 rounded-md cursor-pointer"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          </div>
          <p className="text-xs sm:text-sm text-slate-800 leading-relaxed whitespace-pre-line">
            {aiResponse.answer}
          </p>
        </div>
      )}
    </div>
  );
}
