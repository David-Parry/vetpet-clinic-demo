#!/usr/bin/env node

/**
 * Snyk Report Aggregation Script
 * Merges individual Snyk JSON reports and generates summary
 */

import { readFile, writeFile, readdir } from 'fs/promises';
import { join } from 'path';

const REPORTS_DIR = 'reports';
const SEVERITY_ORDER = ['critical', 'high', 'medium', 'low'];

async function main() {
  console.log('📊 Aggregating Snyk reports...\n');

  try {
    // Read all JSON files from reports directory
    const files = await readdir(REPORTS_DIR);
    const jsonFiles = files.filter(f => f.endsWith('-snyk.json'));

    if (jsonFiles.length === 0) {
      console.warn('⚠️  No Snyk JSON reports found in reports/ directory');
      return;
    }

    const reports = {};
    const aggregatedData = {
      totalVulnerabilities: 0,
      bySeverity: { critical: 0, high: 0, medium: 0, low: 0 },
      byModule: {},
      fixableCount: 0,
      scanDate: new Date().toISOString()
    };

    // Read and parse each report
    for (const file of jsonFiles) {
      const moduleName = file.replace('-snyk.json', '');
      console.log(`  Reading ${file}...`);

      try {
        const content = await readFile(join(REPORTS_DIR, file), 'utf-8');
        const data = JSON.parse(content);
        
        reports[moduleName] = data;

        // Extract vulnerability counts
        const moduleStats = {
          critical: 0,
          high: 0,
          medium: 0,
          low: 0,
          fixable: 0,
          total: 0
        };

        if (data.vulnerabilities && Array.isArray(data.vulnerabilities)) {
          data.vulnerabilities.forEach(vuln => {
            const severity = (vuln.severity || 'low').toLowerCase();
            if (moduleStats[severity] !== undefined) {
              moduleStats[severity]++;
              aggregatedData.bySeverity[severity]++;
            }
            moduleStats.total++;
            aggregatedData.totalVulnerabilities++;

            if (vuln.isUpgradable || vuln.isPatchable) {
              moduleStats.fixable++;
              aggregatedData.fixableCount++;
            }
          });
        }

        aggregatedData.byModule[moduleName] = moduleStats;
      } catch (err) {
        console.warn(`  ⚠️  Failed to parse ${file}: ${err.message}`);
      }
    }

    // Generate Markdown summary
    const markdown = generateMarkdownSummary(aggregatedData);
    await writeFile(join(REPORTS_DIR, 'snyk-summary.md'), markdown);
    console.log('\n✅ Generated: reports/snyk-summary.md');

    // Save aggregated JSON
    await writeFile(
      join(REPORTS_DIR, 'snyk-summary.json'),
      JSON.stringify(aggregatedData, null, 2)
    );
    console.log('✅ Generated: reports/snyk-summary.json');

    // Print summary to console
    console.log('\n' + markdown);

  } catch (err) {
    console.error('❌ Error aggregating reports:', err.message);
    process.exit(1);
  }
}

function generateMarkdownSummary(data) {
  let md = '# Snyk Security Scan Summary\n\n';
  md += `**Scan Date:** ${new Date(data.scanDate).toLocaleString()}\n\n`;

  // Overall statistics
  md += '## Overall Statistics\n\n';
  md += `- **Total Vulnerabilities:** ${data.totalVulnerabilities}\n`;
  md += `- **Fixable Issues:** ${data.fixableCount}\n`;
  md += `- **Fix Rate:** ${data.totalVulnerabilities > 0 ? Math.round((data.fixableCount / data.totalVulnerabilities) * 100) : 0}%\n\n`;

  // Severity breakdown
  md += '## Severity Breakdown\n\n';
  md += '| Severity | Count | Percentage |\n';
  md += '|----------|-------|------------|\n';
  
  SEVERITY_ORDER.forEach(severity => {
    const count = data.bySeverity[severity];
    const percentage = data.totalVulnerabilities > 0 
      ? Math.round((count / data.totalVulnerabilities) * 100) 
      : 0;
    const icon = getSeverityIcon(severity);
    md += `| ${icon} ${severity.charAt(0).toUpperCase() + severity.slice(1)} | ${count} | ${percentage}% |\n`;
  });

  md += '\n';

  // Module breakdown
  md += '## Module Breakdown\n\n';
  md += '| Module | Critical | High | Medium | Low | Total | Fixable |\n';
  md += '|--------|----------|------|--------|-----|-------|----------|\n';

  Object.entries(data.byModule).forEach(([module, stats]) => {
    md += `| ${module} | ${stats.critical} | ${stats.high} | ${stats.medium} | ${stats.low} | ${stats.total} | ${stats.fixable} |\n`;
  });

  md += '\n';

  // Recommendations
  md += '## Recommendations\n\n';
  
  if (data.bySeverity.critical > 0) {
    md += `⚠️ **CRITICAL:** ${data.bySeverity.critical} critical vulnerabilities found. Address immediately!\n\n`;
  }
  
  if (data.bySeverity.high > 0) {
    md += `⚠️ **HIGH:** ${data.bySeverity.high} high severity vulnerabilities found. Prioritize fixes.\n\n`;
  }

  if (data.fixableCount > 0) {
    md += `✅ ${data.fixableCount} vulnerabilities can be fixed by updating dependencies.\n\n`;
  }

  md += '### Next Steps\n\n';
  md += '1. Review individual module reports for detailed vulnerability information\n';
  md += '2. Update dependencies to fix upgradable vulnerabilities\n';
  md += '3. Apply patches where available\n';
  md += '4. For unfixable issues, assess risk and implement mitigations\n';
  md += '5. Re-run scans after applying fixes to verify resolution\n\n';

  md += '---\n';
  md += '*Generated by Snyk Report Aggregation Script*\n';

  return md;
}

function getSeverityIcon(severity) {
  const icons = {
    critical: '🔴',
    high: '🟠',
    medium: '🟡',
    low: '🟢'
  };
  return icons[severity] || '⚪';
}

main().catch(err => {
  console.error('Fatal error:', err);
  process.exit(1);
});
