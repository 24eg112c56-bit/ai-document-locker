import { useState } from 'react';
import {
  X,
  Sparkles,
  Download,
  Share2,
  Calendar,
  DollarSign,
  Users,
  FileText,
  RefreshCw,
  Tag,
  CheckCircle2,
  Info
} from 'lucide-react';
import api from '../services/api';

export default function DocumentDetailModal({ doc, isOpen, onClose, onShare, onDownload, onDocumentUpdated }) {
  const [reprocessing, setReprocessing] = useState(false);
  const [activeTab, setActiveTab] = useState('ai'); // 'ai' | 'text' | 'details'
  const [currentDoc, setCurrentDoc] = useState(doc);

  if (!isOpen || !doc) return null;

  const targetDoc = currentDoc || doc;

  let metadata = {};
  try {
    if (targetDoc.keyEntitiesJson) {
      metadata = JSON.parse(targetDoc.keyEntitiesJson);
    }
  } catch (e) {
    metadata = {};
  }

  const handleReprocess = async () => {
    setReprocessing(true);
    try {
      const res = await api.post(`/ai/documents/${targetDoc.id}/reprocess`);
      setCurrentDoc(res.data);
      if (onDocumentUpdated) onDocumentUpdated(res.data);
    } catch (err) {
      alert('Could not reprocess with AI: ' + (err.response?.data?.message || err.message));
    } finally {
      setReprocessing(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs">
      <div className="bg-white rounded-2xl max-w-3xl w-full max-h-[90vh] border border-slate-200 shadow-xl overflow-hidden flex flex-col animate-in fade-in zoom-in-95 duration-150">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 bg-slate-50/50">
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold px-2.5 py-0.5 rounded-full bg-indigo-100 text-indigo-700 uppercase tracking-wide">
                {targetDoc.category}
              </span>
              <span className="text-xs text-slate-400 font-mono">ID #{targetDoc.id}</span>
            </div>
            <h3 className="text-lg font-bold text-slate-900 mt-1">{targetDoc.title}</h3>
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => onShare(targetDoc)}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-100 border border-slate-200 rounded-lg transition-colors cursor-pointer"
            >
              <Share2 className="w-3.5 h-3.5" />
              <span>Share</span>
            </button>
            <button
              onClick={() => onDownload(targetDoc)}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-xs transition-colors cursor-pointer"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Download</span>
            </button>
            <button
              onClick={onClose}
              className="text-slate-400 hover:text-slate-600 p-1.5 rounded-lg hover:bg-slate-100 transition-colors cursor-pointer"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="flex items-center px-6 border-b border-slate-200 gap-6 text-xs font-bold">
          <button
            onClick={() => setActiveTab('ai')}
            className={`py-3 border-b-2 flex items-center gap-1.5 cursor-pointer transition-all ${
              activeTab === 'ai'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>AI Insights & Entities</span>
          </button>
          <button
            onClick={() => setActiveTab('text')}
            className={`py-3 border-b-2 flex items-center gap-1.5 cursor-pointer transition-all ${
              activeTab === 'text'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <FileText className="w-3.5 h-3.5" />
            <span>Extracted Text</span>
          </button>
          <button
            onClick={() => setActiveTab('details')}
            className={`py-3 border-b-2 flex items-center gap-1.5 cursor-pointer transition-all ${
              activeTab === 'details'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Info className="w-3.5 h-3.5" />
            <span>File Metadata</span>
          </button>
        </div>

        {/* Tab Content Body */}
        <div className="p-6 overflow-y-auto flex-1 space-y-6">
          {activeTab === 'ai' && (
            <div className="space-y-5">
              {/* Executive Summary Card */}
              <div className="p-5 rounded-2xl bg-amber-50/60 border border-amber-200/80">
                <div className="flex items-center justify-between pb-3 border-b border-amber-200/60 mb-3">
                  <div className="flex items-center gap-2">
                    <Sparkles className="w-4 h-4 text-amber-600" />
                    <h4 className="text-sm font-bold text-amber-950">AI Executive Summary</h4>
                  </div>
                  <button
                    onClick={handleReprocess}
                    disabled={reprocessing}
                    className="inline-flex items-center gap-1.5 px-2.5 py-1 text-xs font-semibold text-amber-900 bg-amber-100 hover:bg-amber-200 rounded-lg transition-colors cursor-pointer"
                  >
                    <RefreshCw className={`w-3 h-3 ${reprocessing ? 'animate-spin' : ''}`} />
                    <span>{reprocessing ? 'Re-analyzing...' : 'Refresh AI'}</span>
                  </button>
                </div>
                <p className="text-xs text-slate-700 leading-relaxed">
                  {targetDoc.summary || 'Summary unavailable.'}
                </p>
              </div>

              {/* Extracted Key Entities */}
              <div>
                <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3">
                  Extracted Structured Data
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                  {/* Dates */}
                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200">
                    <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-700 mb-2">
                      <Calendar className="w-3.5 h-3.5 text-indigo-600" />
                      <span>Dates Found</span>
                    </div>
                    {metadata?.dates && Array.isArray(metadata.dates) && metadata.dates.length > 0 ? (
                      <ul className="text-xs space-y-1 text-slate-600 font-mono">
                        {metadata.dates.map((d, i) => (
                          <li key={i}>• {d}</li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-xs text-slate-400 italic">None detected</p>
                    )}
                  </div>

                  {/* Amounts */}
                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200">
                    <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-700 mb-2">
                      <DollarSign className="w-3.5 h-3.5 text-emerald-600" />
                      <span>Monetary Figures</span>
                    </div>
                    {metadata?.amounts && Array.isArray(metadata.amounts) && metadata.amounts.length > 0 ? (
                      <ul className="text-xs space-y-1 text-slate-600 font-mono">
                        {metadata.amounts.map((a, i) => (
                          <li key={i}>• {a}</li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-xs text-slate-400 italic">None detected</p>
                    )}
                  </div>

                  {/* Parties / Organizations */}
                  <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200">
                    <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-700 mb-2">
                      <Users className="w-3.5 h-3.5 text-purple-600" />
                      <span>Identified Parties</span>
                    </div>
                    {metadata?.parties && Array.isArray(metadata.parties) && metadata.parties.length > 0 ? (
                      <ul className="text-xs space-y-1 text-slate-600">
                        {metadata.parties.map((p, i) => (
                          <li key={i}>• {p}</li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-xs text-slate-400 italic">None detected</p>
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'text' && (
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-bold text-slate-600">Extracted Plain Text (PDFBox OCR / Text Parser)</span>
                <span className="text-[11px] text-slate-400 font-mono">
                  {targetDoc.extractedText ? targetDoc.extractedText.length : 0} characters
                </span>
              </div>
              <pre className="p-4 bg-slate-900 text-slate-100 rounded-xl text-xs font-mono whitespace-pre-wrap max-h-96 overflow-y-auto leading-relaxed border border-slate-800">
                {targetDoc.extractedText || 'No text extracted from this file.'}
              </pre>
            </div>
          )}

          {activeTab === 'details' && (
            <div className="grid grid-cols-2 gap-4 text-xs">
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-slate-400 font-semibold block">Original Filename</span>
                <span className="text-slate-800 font-mono break-all mt-1 block">{targetDoc.fileName}</span>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-slate-400 font-semibold block">Content Type</span>
                <span className="text-slate-800 font-mono mt-1 block">{targetDoc.fileType}</span>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-slate-400 font-semibold block">Uploaded At</span>
                <span className="text-slate-800 mt-1 block">{new Date(targetDoc.createdAt).toLocaleString()}</span>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-slate-400 font-semibold block">Tags</span>
                <span className="text-slate-800 mt-1 block">{targetDoc.tags || 'None'}</span>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
